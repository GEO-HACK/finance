//import the java utilities and import necessary classes and interfaces from the javax.swing package and java.awt package
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

//Declaring an NHIF interface that is a method that calculates the NHIF amount based on the payment amount entered
interface NHIF {
    double calculateNHIF(double totalBill);
}
//Patient details that will be requires
class Patient {
    private String name;
    private int age;
    private ArrayList<Payment> payments;
    private static JTextArea outputArea; //This will display patient details

    //' this ' constructor initializes the details for a Patient object
    public Patient(String name, int age) {
        this.name = name;
        this.age = age;
        this.payments = new ArrayList<>();
    }

    //Method for displaying patient details
    public static void setOutputArea(JTextArea area) {
        outputArea = area;
    }

    //This Method creates and displays a form for adding a new Patient using a JFrame
    public static void createAndDisplayPatient(ArrayList<Patient> patientsList) {
        JFrame frame = new JFrame("Add Patient");
        frame.setSize(400, 250);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //Creating a Panel in the GUI for the 'add Patient' button with specific x any y dimensions of its positioning on the form
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5)); // GridLayout with 4 rows, 2 columns, and spacing between components
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding to the form panel

        //Creating JLabels and JTextFields for entering patients details
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        JLabel ageLabel = new JLabel("Age:");
        JTextField ageField = new JTextField();
        JLabel paymentLabel = new JLabel("Payment:");
        JTextField paymentField = new JTextField();

        //Creating JButtons for Adding a Patient , clearing the form and exiting the Application
        JButton addButton = new JButton("Add");
        JButton clearButton = new JButton("Clear");
        JButton exitButton = new JButton("Exit");

        //Adding the Action Listener to the clearButton to clear the input field when clicked
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameField.setText("");
                ageField.setText("");
                paymentField.setText("");
            }
        });

        //Adding the Action Listener to the exitButton to close the JFrame when clicked
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        //Adding the labels,text fields,clear button, and exit button to the formPanel.
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(ageLabel);
        formPanel.add(ageField);
        formPanel.add(paymentLabel);
        formPanel.add(paymentField);
        formPanel.add(clearButton);
        formPanel.add(exitButton);

        //Adding the Action Listener to the addButton to handle adding a new Patient when clicked
        //Also Retrieves the values entered in the input Fields
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                double paymentAmount = Double.parseDouble(paymentField.getText());

                //This calculates the NHIF amount, net payment, creates a new Payment object, adds it to the patient's payment list, adds the patient to the list of patients, displays patient details, and disposes of the JFrame.
                double nhifAmount = new NHIFImplementation().calculateNHIF(paymentAmount);
                double netPayment = paymentAmount - nhifAmount;

                Payment payment = new Payment(netPayment, nhifAmount);
                Patient patient = new Patient(name, age);
                patient.addPayment(payment);
                patientsList.add(patient);
                displayPatientDetails(patientsList);
                frame.dispose();
            }
        });

        //Adding the formPanel and addButton to the mainPanel.
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(addButton, BorderLayout.SOUTH);

        //Adding the mainPanel to the JFrame's content pane and makes the JFrame visible.
        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
    }

    //This method displays patient details in the JTextArea.
    // It iterates over the list of patients, retrieves their details, formats them, and updates the JTextArea.
    public static void displayPatientDetails(ArrayList<Patient> patientsList) {
        StringBuilder details = new StringBuilder("Patients Details:\n");
        for (int i = 0; i < patientsList.size(); i++) {
            Patient patient = patientsList.get(i);
            details.append("Patient ").append(i + 1).append(": ");
            details.append("Name: ").append(patient.getName()).append(", Age: ").append(patient.getAge());
            if (!patient.getPayments().isEmpty()) {
                Payment payment = patient.getPayments().get(0); // Assuming only one payment per patient
                double netPayment = Double.parseDouble(String.format("%.4f", payment.getNetPayment()));
                double nhifAmount = Double.parseDouble(String.format("%.4f", payment.getNhifAmount()));
                details.append(", Net Payment: ").append(netPayment);
                details.append(", NHIF Amount: ").append(nhifAmount);
                details.append(", Date: ").append(payment.getLastUpdatedDateFormatted());
            }
            details.append("\n");
        }
        if (outputArea != null) {
            outputArea.setText(details.toString());
        }
    }

    //These are getter and setter methods for accessing and modifying the properties of a Patient object.
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public ArrayList<Payment> getPayments() {
        return payments;
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
    }
}

class Payment {
    private double netPayment;
    private double nhifAmount;
    private LocalDateTime lastUpdatedDateTime;

    //Constructor that initializes the Payment object
    public Payment(double netPayment, double nhifAmount) {
        this.netPayment = netPayment;
        this.nhifAmount = nhifAmount;
        this.lastUpdatedDateTime = LocalDateTime.now();
    }

    public double getNetPayment() {
        return netPayment;
    }

    public double getNhifAmount() {
        return nhifAmount;
    }

    //Automatic Timestamp that is added after patients details
    public LocalDateTime getLastUpdatedDateTime() {
        return lastUpdatedDateTime;
    }

    public String getLastUpdatedDateFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return lastUpdatedDateTime.format(formatter);
    }
}

//calculates the NHIF amount based on the payment amount entered
class NHIFImplementation implements NHIF {
    @Override
    public double calculateNHIF(double totalBill) {
        return totalBill * 0.1; // Assuming NHIF rate is 10%
    }
}

public class HospitalManagementGUI extends JFrame {
    private JTextArea outputArea;
    private JButton addPatientButton;
    private ArrayList<Patient> patientsList = new ArrayList<>();

    public HospitalManagementGUI() {
        setTitle("Hospital Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addPatientButton = new JButton("Add Patient");

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        Patient.setOutputArea(outputArea);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addPatientButton);

        addPatientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Patient.createAndDisplayPatient(patientsList);
            }
        });

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(outputArea);

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HospitalManagementGUI().setVisible(true);
        });
}
}
