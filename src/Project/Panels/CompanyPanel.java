package Project.Panels;

import javax.swing.*;
import java.sql.*;

import Project.Authentication.Collection;
import Project.Main.GUIDesign;

import java.awt.*;
import java.awt.event.*;

public class CompanyPanel implements ActionListener {
    private JButton signupButton, loginButton, backButtonCompany, postButton, viewButton, backButton;
    private JPanel companyPanel, companyLoggedInPanel, mainPanel, postPanel;
    Connection connection = GUIDesign.connection;
    private Collection conn = new Collection();
    public static String returnedString;
    public CardLayout card;

    /**
     * Initializes components for the company panel.
     */
    public void companyComponent() {
        signupButton = new JButton("Signup");
        signupButton.setActionCommand("companySignup");
        loginButton = new JButton("Login");
        backButtonCompany = new JButton("← Back");

        // Use custom panel with background image
        companyPanel = new ImagePanel("CompanyBack.jpg", new FlowLayout());
        companyPanel.add(signupButton);
        companyPanel.add(loginButton);
        companyPanel.add(backButtonCompany);

        // Add action listeners to buttons
        signupButton.addActionListener(this);
        loginButton.addActionListener(this);
        backButtonCompany.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();

        // Handle different button actions
        switch (command) {
            case "Login":
                if (conn.login()) {
                    returnedString = conn.retunString();
                    postPanel = new PostPanel(connection, returnedString, null, 0, null).getPanel();
                    mainPanel.add(postPanel, "Post");
                    card.show(mainPanel, "LoggedIn");
                }
                break;
            case "Post":
                card.show(mainPanel, "Post");
                break;
            case "View":
                mainPanel.add(new ViewPanel().getResultPanel(), "View");
                mainPanel.revalidate();
                mainPanel.repaint();
                card.show(mainPanel, "View");
                break;
            case "companySignup":
                conn.cSignup();
                break;
            case "Back":
                card.previous(mainPanel);
                break;
            case "← Back":
                new GUIDesign().actionPerformed(ae);
                break;
            case "Back1":
                card.show(mainPanel, "Company");
                break;
            default:
                System.out.println("Unknown command: " + command);
                break;
        }
    }

    /**
     * Returns the main panel for the company UI.
     * 
     * @return the main JPanel
     */
    public JPanel getPanel() {
        companyComponent();
        card = new CardLayout(); // Initialize CardLayout
        mainPanel = new JPanel(card); // Initialize mainPanel with CardLayout
        mainPanel.add(companyPanel, "Company");
        mainPanel.add(companyLoggedIn(), "LoggedIn");
        return mainPanel;
    }

    /**
     * Creates and returns the panel for the company logged-in view.
     * 
     * @return the company logged-in JPanel
     */
    private JPanel companyLoggedIn() {
        postButton = new JButton("Post");
        postButton.addActionListener(this);
        viewButton = new JButton("View");
        postButton.addActionListener(this);
        backButton = new JButton("← Back");
        backButton.setActionCommand("Back");
        backButton.addActionListener(this);

        // Use custom panel with background image
        companyLoggedInPanel = new ImagePanel("backcom.jpg", new FlowLayout());
        companyLoggedInPanel.add(postButton);
        companyLoggedInPanel.add(viewButton);
        companyLoggedInPanel.add(backButton);

        postButton.addActionListener(this);
        viewButton.addActionListener(this);

        return companyLoggedInPanel;
    }
}
