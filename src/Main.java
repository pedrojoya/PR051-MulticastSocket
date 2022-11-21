import employee.Employee;
import server.Employer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

class Main {


    public static void main(String[] args) throws InterruptedException {
        try {
            InetAddress multicastSocketAddress = InetAddress.getByName("224.0.0.1");
            int multicastSocketPort = 6789;
            int numberOfEmployees = 5;
            Thread[] employees = new Thread[numberOfEmployees];
            Thread employer = new Thread(new Employer(multicastSocketAddress, multicastSocketPort));
            employer.start();
            for (int i = 0; i < 5; i++) {
                employees[i] = new Thread(new Employee(i, multicastSocketAddress, multicastSocketPort));
                employees[i].start();
            }
            TimeUnit.SECONDS.sleep(5);

            employer.join();
            for (Thread employee : employees) {
                employee.join();
            }
            System.out.println("Main: Finished");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

}
