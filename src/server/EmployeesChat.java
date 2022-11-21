package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class EmployeesChat implements Runnable {

    private final int port;
    private final InetAddress group;

    public EmployeesChat(InetAddress group, int port) {
        this.group = group;
        this.port = port;
    }

    @Override
    public void run() {
        try(MulticastSocket socket = new MulticastSocket(port)) {
            socket.joinGroup(group);
            receiveMulticastMessages(socket, group);
        } catch (IOException e) {
            System.out.println("Employer - Input/Output error");
        }
    }

    private void receiveMulticastMessages(MulticastSocket socket, InetAddress group) throws IOException {
        byte[] buffer = new byte[1000];
        String receivedMesssage;
        while (!Thread.currentThread().isInterrupted()) {
            DatagramPacket inputDatagramPacket =
                    new DatagramPacket(buffer, buffer.length);
            socket.receive(inputDatagramPacket);
            receivedMesssage = new String(inputDatagramPacket.getData(), 0, inputDatagramPacket.getLength());
            System.out.printf("Employer - Message received: %s\n", receivedMesssage);
            if (receivedMesssage.equals("Go home")) break;
        }
        socket.leaveGroup(group);
        System.out.println("Employer - Left group");
    }

}
