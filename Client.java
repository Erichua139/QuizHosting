/*
 *Client.java 
 * Gets questions from the Server. Get the user input and returns the answer to the server. 
 * Once the Client finished all the questions server will return a scoreboard and the client will exit
 * Eric Hua
 * 100777617
 */
import java.io.*;
import java.net.*;

//Client Class that recieves the questions
public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        // Connecting to the server. Readying Reader and Writers
        try (Socket socket = new Socket(HOST, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {
            //Welcome Message and Getting user names and category
            System.out.println("Connected to the quiz server.");
            System.out.print("Enter your name: ");
            String name = stdIn.readLine();
            out.println(name); 
            // Read and display available quiz categories
            String availableCategories = in.readLine();
            System.out.println(availableCategories);

            // Prompt the user to select a category
            System.out.print("Enter quiz category: ");
            String selectedCategory = stdIn.readLine();
            out.println(selectedCategory);
            String fromServer;

            //Reading it questions
            while ((fromServer = in.readLine()) != null) { //Read text from the server
                System.out.println("Server: " + fromServer);

                if (fromServer.contains("?")) { //Assume it's a question if it contains a question mark
                    System.out.print("Answer: ");
                    //Get the ansewr
                    String answer = stdIn.readLine();
                    if (answer != null) {
                        out.println(answer); //Send the answer to the server
                    }
                }
            }
        //Exceptions
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + HOST);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    HOST);
            System.exit(1);
        }
    }
}
