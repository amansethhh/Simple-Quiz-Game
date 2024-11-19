import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleQuizGame {

    private JFrame frame;
    private JPanel questionPanel, answerPanel, timerPanel, lifelinePanel;
    private JLabel questionLabel, timerLabel, scoreLabel, lifelineLabel;
    private JRadioButton[] answerButtons;
    private ButtonGroup answerGroup;
    private JButton submitButton, fiftyFiftyButton, skipButton, extendTimeButton;
    private String[] questions = {
            "What is the capital of France?",
            "Who wrote 'Hamlet'?",
            "What is the largest planet in our solar system?",
            "In which year did World War II end?",
            "Who painted the Mona Lisa?",
            "Which element is represented by the symbol 'O'?",
            "What is the square root of 64?",
            "What is the chemical formula of water?",
            "What is the smallest prime number?",
            "Which planet is known as the Red Planet?",
            "What is the largest mammal?",
            "How many continents are there?",
            "What is the capital of Japan?",
            "Who discovered gravity?",
            "Which country is the largest by area?",
            "In which year was the first iPhone released?",
            "What is the longest river in the world?",
            "Who is known as the father of computers?",
            "Which organ in the human body is primarily responsible for pumping blood?",
            "Which is the tallest mountain in the world?"
    };
    private String[][] options = {
            {"Paris", "Rome", "Berlin", "Madrid"},
            {"Charles Dickens", "J.K. Rowling", "William Shakespeare", "Mark Twain"},
            {"Earth", "Jupiter", "Saturn", "Mars"},
            {"1945", "1918", "1939", "1965"},
            {"Pablo Picasso", "Leonardo da Vinci", "Vincent van Gogh", "Claude Monet"},
            {"Oxygen", "Gold", "Iron", "Carbon"},
            {"6", "7", "8", "9"},
            {"H2O", "CO2", "O2", "NaCl"},
            {"0", "1", "2", "3"},
            {"Venus", "Mars", "Jupiter", "Saturn"},
            {"Elephant", "Blue Whale", "Giraffe", "Human"},
            {"5", "6", "7", "8"},
            {"Beijing", "Tokyo", "Seoul", "Bangkok"},
            {"Albert Einstein", "Isaac Newton", "Galileo Galilei", "Marie Curie"},
            {"Russia", "Canada", "USA", "China"},
            {"2005", "2006", "2007", "2008"},
            {"Amazon", "Nile", "Yangtze", "Mississippi"},
            {"Alan Turing", "Charles Babbage", "Bill Gates", "Steve Jobs"},
            {"Heart", "Liver", "Kidney", "Brain"},
            {"K2", "Everest", "Kangchenjunga", "Makalu"}
    };
    private char[] answers = {'a', 'c', 'b', 'a', 'b', 'a', 'c', 'a', 'b', 'b', 'b', 'c', 'b', 'b', 'a', 'c', 'b', 'b', 'a', 'b'};
    private List<Integer> questionOrder;
    private int currentQuestion = 0;
    private int score = 0;
    private Timer swingTimer;
    private int timeLeft = 15;
    private boolean answered = false;

    // Lifeline flags
    private boolean usedFiftyFifty = false;
    private boolean usedSkip = false;
    private boolean usedExtendTime = false;

    // New variable to store the player's name
    private String username;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimpleQuizGame().createLoginPage());
    }

    // Create the login page
    private void createLoginPage() {
        frame = new JFrame("Quiz Game Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 350);
        frame.setLayout(new BorderLayout());

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        loginPanel.setBackground(new Color(60, 63, 65));

        JLabel titleLabel = new JLabel("Welcome to the Quiz Game!");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("Enter your name:");
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(300, 30));

        JButton startButton = new JButton("Start Quiz");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setBackground(new Color(34, 167, 240));
        startButton.setForeground(Color.WHITE);
        startButton.setFont(new Font("SansSerif", Font.BOLD, 16));

        startButton.addActionListener(e -> {
            String enteredName = nameField.getText();
            if (!enteredName.isEmpty()) {
                username = enteredName; // Store the username
                frame.getContentPane().removeAll();
                frame.repaint();
                createRulesPage(username);  // Navigate to rules page
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter your name.");
            }
        });

        loginPanel.add(titleLabel);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(nameLabel);
        loginPanel.add(nameField);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(startButton);

        frame.add(loginPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // Create the game rules page
    private void createRulesPage(String username) {
        frame.getContentPane().removeAll();
        frame.repaint();
        
        JPanel rulesPanel = new JPanel();
        rulesPanel.setLayout(new BoxLayout(rulesPanel, BoxLayout.Y_AXIS));
        rulesPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        rulesPanel.setBackground(new Color(60, 63, 65));

        JLabel rulesTitleLabel = new JLabel("Quiz Game Rules");
        rulesTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        rulesTitleLabel.setForeground(Color.WHITE);
        rulesTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea rulesText = new JTextArea(10, 40);
        rulesText.setText("1. You have 15 seconds to answer each question.\n"
                + "2. You can use three lifelines: 50/50, Skip, and Extend Time.\n"
                + "3. 50/50 removes two incorrect answers.\n"
                + "4. Skip allows you to skip the current question.\n"
                + "5. Extend Time adds 10 more seconds to the clock.\n"
                + "6. Your score will be displayed at the end of the quiz.");
        rulesText.setFont(new Font("SansSerif", Font.PLAIN, 16));
        rulesText.setForeground(Color.WHITE);
        rulesText.setBackground(new Color(60, 63, 65));
        rulesText.setEditable(false);

        JButton startQuizButton = new JButton("Start Quiz");
        startQuizButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startQuizButton.setBackground(new Color(34, 167, 240));
        startQuizButton.setForeground(Color.WHITE);
        startQuizButton.setFont(new Font("SansSerif", Font.BOLD, 16));

        startQuizButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.repaint();
            createQuizPage(username);  // Proceed to quiz page
        });

        rulesPanel.add(rulesTitleLabel);
        rulesPanel.add(Box.createVerticalStrut(20));
        rulesPanel.add(new JScrollPane(rulesText));
        rulesPanel.add(Box.createVerticalStrut(20));
        rulesPanel.add(startQuizButton);

        frame.add(rulesPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    // Create the quiz page
    private void createQuizPage(String username) {
        frame.getContentPane().removeAll();
        frame.repaint();

        frame.setLayout(new BorderLayout());
        frame.setSize(600, 400);

        // Randomize question order
        questionOrder = new ArrayList<>();
        for (int i = 0; i < questions.length; i++) {
            questionOrder.add(i);
        }
        Collections.shuffle(questionOrder);

        // Create panels for question, answers, timer, and lifelines
        questionPanel = new JPanel();
        questionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        questionPanel.setBackground(new Color(60, 63, 65));
        questionLabel = new JLabel();
        questionLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        questionLabel.setForeground(Color.WHITE);
        questionPanel.add(questionLabel);

        answerPanel = new JPanel();
        answerPanel.setLayout(new BoxLayout(answerPanel, BoxLayout.Y_AXIS));
        answerPanel.setBackground(new Color(60, 63, 65));
        answerGroup = new ButtonGroup();
        answerButtons = new JRadioButton[4];

        for (int i = 0; i < 4; i++) {
            answerButtons[i] = new JRadioButton();
            answerButtons[i].setFont(new Font("SansSerif", Font.PLAIN, 16));
            answerButtons[i].setForeground(Color.WHITE);
            answerButtons[i].setBackground(new Color(60, 63, 65));
            answerGroup.add(answerButtons[i]);
            answerPanel.add(answerButtons[i]);
        }

        submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(34, 167, 240));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        submitButton.addActionListener(e -> checkAnswer());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        timerPanel = new JPanel();
        timerPanel.setBackground(new Color(60, 63, 65));
        timerLabel = new JLabel("Time Left: 15");
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        timerLabel.setForeground(Color.WHITE);
        timerPanel.add(timerLabel);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        scoreLabel.setForeground(Color.WHITE);

        lifelinePanel = new JPanel();
        lifelinePanel.setLayout(new GridLayout(1, 3, 10, 10));
        fiftyFiftyButton = new JButton("50/50");
        fiftyFiftyButton.addActionListener(e -> useFiftyFifty());
        skipButton = new JButton("Skip");
        skipButton.addActionListener(e -> useSkip());
        extendTimeButton = new JButton("Extend Time");
        extendTimeButton.addActionListener(e -> useExtendTime());

        lifelinePanel.add(fiftyFiftyButton);
        lifelinePanel.add(skipButton);
        lifelinePanel.add(extendTimeButton);

        bottomPanel.add(timerPanel, BorderLayout.NORTH);
        bottomPanel.add(submitButton, BorderLayout.SOUTH);
        bottomPanel.add(lifelinePanel, BorderLayout.CENTER);

        frame.add(questionPanel, BorderLayout.NORTH);
        frame.add(answerPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        nextQuestion(); // Load the first question
    }

    private void nextQuestion() {
        if (currentQuestion >= questions.length) {
            showScore();
            return;
        }

        int questionIndex = questionOrder.get(currentQuestion);
        questionLabel.setText(questions[questionIndex]);

        for (int i = 0; i < 4; i++) {
            answerButtons[i].setText(options[questionIndex][i]);
            answerButtons[i].setActionCommand(String.valueOf((char) ('a' + i)));
            answerButtons[i].setSelected(false);
            answerButtons[i].setEnabled(true);
        }

        // Reset lifelines availability
        fiftyFiftyButton.setEnabled(!usedFiftyFifty);
        skipButton.setEnabled(!usedSkip);
        extendTimeButton.setEnabled(!usedExtendTime);

        // Reset timer
        timeLeft = 15;
        timerLabel.setText("Time Left: " + timeLeft);
        if (swingTimer != null) {
            swingTimer.stop();
        }
        startTimer();

        currentQuestion++;
    }

    private void startTimer() {
        swingTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerLabel.setText("Time Left: " + timeLeft);
                if (timeLeft <= 0) {
                    swingTimer.stop();
                    checkAnswer(); // Auto-submit if time runs out
                }
            }
        });
        swingTimer.start();
    }

    private void checkAnswer() {
        if (swingTimer != null) {
            swingTimer.stop();
        }

        int questionIndex = questionOrder.get(currentQuestion - 1);
        String selectedAnswer = answerGroup.getSelection() != null
                ? answerGroup.getSelection().getActionCommand()
                : "";

        if (selectedAnswer.equals(String.valueOf(answers[questionIndex]))) {
            score++;
            scoreLabel.setText("Score: " + score);
        }

        nextQuestion(); // Load next question
    }

    private void useFiftyFifty() {
        if (usedFiftyFifty) return;

        int questionIndex = questionOrder.get(currentQuestion - 1);
        List<Integer> wrongAnswers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (options[questionIndex][i].charAt(0) != answers[questionIndex]) {
                wrongAnswers.add(i);
            }
        }

        // Randomly remove two wrong answers
        Collections.shuffle(wrongAnswers);
        for (int i = 0; i < 2; i++) {
            answerButtons[wrongAnswers.get(i)].setEnabled(false);
        }

        usedFiftyFifty = true;
        fiftyFiftyButton.setEnabled(false);
    }

    private void useSkip() {
        if (usedSkip) return;
        usedSkip = true;
        skipButton.setEnabled(false);
        nextQuestion(); // Skip current question
    }

    private void useExtendTime() {
        if (usedExtendTime) return;
        timeLeft += 10;
        timerLabel.setText("Time Left: " + timeLeft);
        usedExtendTime = true;
        extendTimeButton.setEnabled(false);
    }

    private void showScore() {
        JOptionPane.showMessageDialog(frame, "Quiz Over! Your final score is: " + score);
        saveScoreToFile(username, score); // Save the score to a file
        frame.dispose();
    }

    private void saveScoreToFile(String name, int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("quiz_scores.txt", true))) {
            writer.write("Name: " + name + ", Score: " + score);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error saving score: " + e.getMessage());
        }
    }
}