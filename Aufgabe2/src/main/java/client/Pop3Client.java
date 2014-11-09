package client;

import client.requests.ListRequest;
import client.requests.PassRequest;
import client.requests.Request;
import client.requests.UserRequest;
import client.responses.ErrResponse;
import client.responses.OkResponse;
import client.responses.Response;
import client.responses.Responses;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;

/**
 * Created by flbaue on 08.11.14.
 */

public class Pop3Client implements AutoCloseable {

    private Socket connection;
    private Account account;
    private BufferedReader in;
    private BufferedWriter out;
    private Pop3States state;

    public Pop3Client(Account account) {
        this.account = account;
    }

    public void connect() throws IOException {

        requireState(Pop3States.DISCONNECTED);

        SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
        connection = ssf.createSocket(account.getServer(), account.getPort());
        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

        Responses.requireOk(readResponse());

        state = Pop3States.AUTHORIZATION;
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

    public void disconnect() {

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

    public void authorize() {
        requireState(Pop3States.AUTHORIZATION);

        username();
        password();

        state = Pop3States.TRANSACTION;
    }

    private void password() {
        sendRequestAndRequireOk(new PassRequest(account.getPassword()));
    }

    private void username() {
        sendRequestAndRequireOk(new UserRequest(account.getUsername()));
    }

    private void list(){
        requireState(Pop3States.TRANSACTION);

        sendRequest(new ListRequest());
        Response response = readMultilineResponse();
        Responses.requireOk(response);
        
        //TODO handle response data;
        // create list of emails and hand them out

    }


    @Override
    public void close() throws Exception {
        disconnect();
    }

    private void sendRequestAndRequireOk(Request request){
        sendRequest(request);
        Responses.requireOk(readResponse());
    }

    private void sendRequest(Request request) {
        try {
            out.write(request.toStringWithLineEnd());
            out.flush();
        } catch (IOException e) {
            //TODO maybe handle by closing the client?
            throw new RuntimeException("Cannot send request", e);
        }
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

    private Response readMultilineResponse() {
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

}
