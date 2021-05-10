package cs568.project.ClientSideProject;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "speedTestServlet", value = "/speed-test-servlet")
public class HomePageServlet extends HttpServlet {
    private long averagePing;
    private long averageDownloadSpeed;
    private long averageUploadSpeed;
    final String awsMachineIpAddress = "ec2-52-59-90-223.eu-central-1.compute.amazonaws.com";
    Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    {
        try {
            socket = new Socket(awsMachineIpAddress, 9000);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //To connect to a server running on localhost, Download speed on localhost will show 100 Mbps
//        final String awsMachineIpAddress = "localhost";
    final int pingTestTrialTimes = 10;
    final int downloadTestTrialTimes = 2;
    final int uploadTestTrialTimes = 2;
    int testProgress = 0;

    public void init() {
        try {
            System.out.println("Server says: " + ois.readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Sends the required message to the BE server to start the ping test process
        System.out.println("Thank you for using our speed testing service. We hope to see you again.");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        testProgress = 0;
        if (socket.isClosed() || !socket.isConnected()) {
            socket = new Socket(awsMachineIpAddress, 9000);
            System.out.println(socket.getLocalSocketAddress());
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            System.out.println("Server says: " + ois.readUTF());
        }
        averagePing = runPingTest(awsMachineIpAddress, pingTestTrialTimes);
        averageDownloadSpeed = runDownloadTest(oos, ois, downloadTestTrialTimes);
        averageUploadSpeed = runUploadTest(oos, ois, uploadTestTrialTimes);
        sendCloseConnectionMessage(oos, ois);
        socket.close();

        request.setAttribute("averagePing", averagePing);
        request.setAttribute("averageDownloadSpeed", averageDownloadSpeed);
        request.setAttribute("averageUploadSpeed", averageUploadSpeed);

        //Report the test results
        request.getRequestDispatcher("/testResults.jsp").forward(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(testProgress);
    }

    public void destroy() {
    }

    //Sends an ICMP ping to the given IP address to ping it, and calculates and returns the time it took to do that in ms as output to console.
    public long runPingTest(String awsMachineIpAddress, int pingTestTrialTimes) {
        long startTime = 0;
        long endTime = 0;
        boolean isPinged = false;
        int totalPing = 0;
        try {
            for (int i = 0; i < pingTestTrialTimes; i++) {
                System.out.println("Ping " + (i + 1) + " start...");
                startTime = System.currentTimeMillis();
                isPinged = InetAddress.getByName(awsMachineIpAddress).isReachable(2000);
                endTime = System.currentTimeMillis();
                System.out.println("Current ping(" + (i + 1) + "/" + pingTestTrialTimes + "): " + (int) (endTime - startTime) + " ms");
                System.out.println("Ping " + (i + 1) + " end...\n");
                totalPing += (int) (endTime - startTime);
                testProgress++;
            }
        } catch (IOException e) {
            System.out.println("Something went wrong while pinging the server.");
            return -1;
        }
        if (isPinged) {
            return (totalPing / pingTestTrialTimes);
        } else {
            return -1;
        }
    }

    public long runDownloadTest(ObjectOutputStream oos, ObjectInputStream ois, int downloadTestTrialTimes) {
        long startTime = 0;
        long endTime = 0;
        byte[] fileContent = null;
        int averageSpeed = 0;
        int totalDownloadTime = 0;
        try {
            for (int i = 0; i < downloadTestTrialTimes; i++) {
                startTime = System.currentTimeMillis();
                oos.writeUTF("downloadTest");
                oos.flush();

                String receivedMessage = ois.readUTF();
                System.out.println("Server says: " + receivedMessage);
                System.out.println("Download test " + (i + 1) + " start... Beginning to read incoming file.");
                try {
                    fileContent = (byte[]) ois.readObject();
                    System.out.println("Incoming file was successfully read.");
                    //Code to write the received message to a file (will write all 0, since the files do not contain any actual data, they are dummy files)
//                PrintStream fileWriter = new PrintStream(fileName);
//                for (int i = 0; i < fileContent.length; i++)
//                    fileWriter.print(fileContent[i]);
                } catch (ClassNotFoundException e) {
                    System.out.println("Something went wrong while downloading the file from the server.");
                    return -1;
                }

                endTime = System.currentTimeMillis();
                totalDownloadTime += (endTime - startTime);
                assert fileContent != null;
                long bps = (fileContent.length * 8L) / ((endTime - startTime) / 1000);
                long Kbps = bps / 1024;
                long Mbps = Kbps / 1024;
                System.out.println("Current download speed(" + (i + 1) + "/" + downloadTestTrialTimes + "): " + Mbps + " Mbps\n");
                averageSpeed += Mbps;
                testProgress++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        averageSpeed /= downloadTestTrialTimes;
        long averageDownloadTime = totalDownloadTime / downloadTestTrialTimes;
        System.out.println("Average data transfer time (download): " + averageDownloadTime + " ms\n");

        return averageSpeed;
    }

    public long runUploadTest(ObjectOutputStream oos, ObjectInputStream ois, int uploadTestTrialTimes) {
        long startTime = 0;
        long endTime = 0;
        byte[] fileContent = null;
        int averageSpeed = 0;
        int totalUploadTime = 0;
        for (int i = 0; i < uploadTestTrialTimes; i++) {
            try {
                startTime = System.currentTimeMillis();
                oos.writeUTF("uploadTest");
                oos.flush();

                String receivedMessage = ois.readUTF();
                System.out.println("Server says: " + receivedMessage);
                System.out.println("Upload test " + (i + 1) + " start... Beginning to send file.");
                if ("Upload message received.".equals(receivedMessage)) {
                    try {
                        File uploadFile = new File(getClass().getClassLoader().getResource("uploadFile.txt").getFile());
                        fileContent = Files.readAllBytes(uploadFile.toPath());
                        oos.writeObject(fileContent);
                        oos.flush();
                        System.out.println("File was successfully sent.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Something went wrong while uploading the file to the server.");
                    return -1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                endTime = ois.readLong();
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert fileContent != null;
            long bps = (fileContent.length * 8L) / ((endTime - startTime) / 1000);
            long Kbps = bps / 1024;
            long Mbps = Kbps / 1024;
            averageSpeed += Mbps;
            testProgress++;
            System.out.println("Current upload speed(" + (i + 1) + "/" + uploadTestTrialTimes + "): " + Mbps + " Mbps\n");
            totalUploadTime += (endTime - startTime);
        }
        averageSpeed /= uploadTestTrialTimes;
        long averageUploadTime = totalUploadTime / uploadTestTrialTimes;
        System.out.println("Average data transfer time (upload): " + averageUploadTime + " ms\n");
        return averageSpeed;
    }

    public void sendCloseConnectionMessage(ObjectOutputStream oos, ObjectInputStream ois) throws IOException {
        oos.writeUTF("closeConnection");
        oos.flush();
    }
}