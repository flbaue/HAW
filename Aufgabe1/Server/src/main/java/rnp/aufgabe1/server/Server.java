/*
 * Florian Bauer
 * florian.bauer@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by flbaue on 13.10.14.
 */
public class Server implements Runnable {

	private String secretToken;
	private int serverPort = 8080;
	private ServerSocket serverSocket = null;
	private boolean isStopped = false;
	private Map<ConnectionWorker, Thread> connections = new HashMap<>();

	public Server(String secretToken) {
		this.secretToken = secretToken;
	}

	public Server(String secretToken, int serverPort) {
		this.secretToken = secretToken;
		this.serverPort = serverPort;
	}

	@Override
	public void run() {
		openServerSocket();
		while (!isStopped()) {
			Socket clientSocket;
			try {
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				if (isStopped) {
					break;
				} else {
					throw new RuntimeException(
							"Error accepting client connection", e);
				}
			}

			if (!isStopped() && connections.size() < 2) {
				ConnectionWorker connection = new ConnectionWorker(
						clientSocket, this);
				Thread connectionThread = new Thread(connection);
				connectionThread.setName("connectionWorker"
						+ connection.instance);
				connectionThread.start();
				connections.put(connection, connectionThread);

				printNumberOfActiveClients();
			} else {
				try {
					clientSocket.close();
					System.out.println("Client not accepted");
				} catch (IOException e) {
					throw new RuntimeException(
							"Error closing client connection", e);
				}
			}
		}

		closeConnectionsWithTimeout();
		closeServerSocket();

		System.out.println("Server stopped.");
	}

	private void printNumberOfActiveClients() {
		System.out.println("Active clients: " + connections.size());
	}

	private void closeConnectionsWithTimeout() {
		int timeout = 60000;

		while (!connections.isEmpty()) {
			for (ConnectionWorker connection : connections.keySet()) {
				connection.setHasReceivedFalse();
			}

			sleep(timeout);

			for (ConnectionWorker connection : connections.keySet()) {
				if (!connection.isHasReceived()) {
					connection.closeConnection();
				}
			}
		}
	}

	private void sleep(int sleep) {
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void closeServerSocket() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			throw new RuntimeException("Cannot close port " + serverPort, e);
		}
	}

	private void openServerSocket() {
		try {
			this.serverSocket = new ServerSocket(serverPort);
		} catch (IOException e) {
			throw new RuntimeException("Cannot open port " + serverPort, e);
		}
	}

	private boolean isStopped() {
		return this.isStopped;
	}

	private void stop() {
		this.isStopped = true;
	}

	public boolean stop(String secretToken) {
		if (this.secretToken.equals(secretToken)) {
			stop();
			return true;
		}
		return false;
	}

	public void removeConnection(ConnectionWorker connection) {
		connections.remove(connection);
		printNumberOfActiveClients();
		if (isStopped() && connections.isEmpty()) {
			closeServerSocket();
		}
	}
}
