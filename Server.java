
/*
 *Server.java 
 * Connectes to clients and sends questions. Recieves annswers and sends reply.
 * Keeps track of user score reports a score board for that server session.
 * Eric Hua
 * 100777617
 * 
 */
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static List<Question> questionBank;
    private static ConcurrentHashMap<String, Integer> scores;

    public static void main(String[] args) throws IOException {
        // Initialize questions and scores
        questionBank = new ArrayList<>();
        scores = new ConcurrentHashMap<>();

        // Connecting to the Client
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Quiz Server is listening on port " + PORT);
            // Accepting new clients
            while (true) {
                new QuizClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void populateQuestions(String category) {
        // Locate the file
        File file = new File(category + ".txt");

        try (Scanner scanner = new Scanner(file)) {
            // Read file
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // Split line to extract question and answer
                String[] parts = line.split(",", 2); // Split only at the first comma
                if (parts.length >= 2) {
                    String question = parts[0].trim();
                    String answer = parts[1].trim();
                    // Add question to the list
                    if (!question.isEmpty() && !answer.isEmpty()) {
                        questionBank.add(new Question(question, answer));
                    } else {
                        System.out.println("Skipping question with empty question or answer.");
                    }
                } else {
                    System.out.println("Skipping question, Sorry." + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
    }

    // Class to handle the quiz
    private static class QuizClientHandler extends Thread {
        private Socket socket;
        private List<Question> questionBank;
        public QuizClientHandler(Socket socket) {
            this.socket = socket;
            this.questionBank = Server.questionBank;

        }

        public void run() {
            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                //intialize client name and score
                String clientName = in.readLine();
                scores.put(clientName, 0);
                //Geting the Category
                String category = in.readLine();
                if (!Arrays.asList("science", "history", "geography").contains(category)) {
                    out.println("Invalid category. Please restart the quiz and select a valid category.");
                    socket.close();
                    return;
                }

                // Load questions based on the selected category
                questionBank.clear();
                populateQuestions(category);

                // Sending questions to client
                for (Question q : questionBank) {
                    out.println(q.question); 
                    // Getting response
                    String response = in.readLine();
                    //Dealing with the score
                    if (q.answer.equalsIgnoreCase(response)) {
                        scores.put(clientName, scores.get(clientName) + 1);
                        out.println("Correct! Your score is: " + scores.get(clientName));
                    } else {
                        out.println("Wrong! The correct answer is: " + q.answer + ". Your score is: "
                                + scores.get(clientName));
                    }
                    // Send leaderboard
                    out.println("Current standings: " + scores);
                }

                socket.close();
            } catch (IOException e) {
                System.out.println("Exception caught when trying to listen on port "
                        + PORT + " or listening for a connection");
                System.out.println(e.getMessage());
            }
        }
    }

    // Question class to hold question and answer pairs
    private static class Question {
        private String question;
        private String answer;

        public Question(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }
    }
}
