package flaue.pop3proxy.client;

import flaue.pop3proxy.client.requests.ListRequest;
import flaue.pop3proxy.client.requests.PassRequest;
import flaue.pop3proxy.client.requests.Request;
import flaue.pop3proxy.client.requests.UserRequest;
import flaue.pop3proxy.client.responses.ErrResponse;
import flaue.pop3proxy.client.responses.OkResponse;
import flaue.pop3proxy.client.responses.Response;
import flaue.pop3proxy.client.responses.Responses;
import flaue.pop3proxy.common.Account;
import flaue.pop3proxy.common.Mail;
import flaue.pop3proxy.mailstore.MailStore;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by flbaue on 08.11.14.
 */

public class Pop3Client implements AutoCloseable {

    private Socket connection;
    private Account account;
    private BufferedReader in;
    private BufferedWriter out;
    private Pop3States state;
    private MailStore mailStore;

    public Pop3Client(Account account, MailStore mailStore) {
        this.account = account;
        this.mailStore = mailStore;
    }

    public void fetchMails() throws IOException {
        connect();
        authorize();
        Set<MailInfo> mailInfos = list();
        Set<Mail> mails = downloadMails(mailInfos);
        storeMails(mails);
        deleteMailsFromServer(mailInfos);
    }

    private void deleteMailsFromServer(Set<MailInfo> mailInfos) {
        for (MailInfo mailInfo : mailInfos) {
            del(mailInfo);
        }
    }

    private void connect() throws IOException {

        requireState(Pop3States.DISCONNECTED);

        SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
        connection = ssf.createSocket(account.getServer(), account.getPort());
        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

        Responses.requireOk(readResponse());

        state = Pop3States.AUTHORIZATION;
    }

    private void disconnect() {

        prohibitState(Pop3States.DISCONNECTED);

        try {
            in.close();
            out.close();
            connection.close();
        } catch (IOException e) {

        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        state = Pop3States.DISCONNECTED;
    }

    private Set<Mail> downloadMails(Set<MailInfo> mailInfos) {
        Set<Mail> mails = new HashSet<>();

        for (MailInfo mailInfo : mailInfos) {
            Mail mail = retr(mailInfo);
            mails.add(mail);
        }

        return mails;
    }

    private void storeMails(Set<Mail> mails) {
        for (Mail mail : mails) {
            mailStore.storeMail(account, mail);
        }
    }

    private void authorize() {
        requireState(Pop3States.AUTHORIZATION);

        username();
        password();

        state = Pop3States.TRANSACTION;
    }

    private void username() {
        sendRequestAndRequireOk(new UserRequest(account.getUsername()));
    }

    private void password() {
        sendRequestAndRequireOk(new PassRequest(account.getPassword()));
    }

    private Set<MailInfo> list() {
        requireState(Pop3States.TRANSACTION);

        sendRequest(new ListRequest());
        Response response = readMultiLineResponse();

        //TODO
        return null;
    }

    private Mail retr(MailInfo mailInfo) {
        //TODO
        return null;
    }

    private void del(MailInfo mailInfo) {
        //TODO
    }

    private void requireState(Pop3States state) {
        if (this.state != state) {
            throw new IllegalStateException("State required: " + state.name() + " actual: " + state.name());
        }
    }

    private void prohibitState(Pop3States state) {
        if (this.state == state) {
            throw new IllegalStateException("State prohibited: " + state.name());
        }
    }

    private void sendRequest(Request request) {
        try {
            out.write(request.toStringWithLineEnd());
            out.flush();
        } catch (IOException e) {
            //TODO maybe handle by closing the flaue.pop3proxy.client?
            throw new RuntimeException("Cannot send request", e);
        }
    }

    private Response sendRequestAndRequireOk(Request request) {
        sendRequest(request);
        Response response = readResponse();
        Responses.requireOk(response);
        return response;
    }

    private Response readResponse() {
        try {
            String line = "";
            line = in.readLine();
            return Responses.createResponse(line);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read response", e);
        }
    }

    private Response readMultiLineResponse() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String line = in.readLine();

            if (line.startsWith(ErrResponse.COMMAND)) {
                return new ErrResponse(line);
            }

            stringBuilder.append(line);
            while (!line.equals(".")) {
                line = in.readLine();
                stringBuilder.append(line);
            }
            return new OkResponse(stringBuilder.toString());

        } catch (IOException e) {
            throw new RuntimeException("Cannot read response", e);
        }
    }

    @Override
    public void close() throws Exception {
        disconnect();
    }
}
