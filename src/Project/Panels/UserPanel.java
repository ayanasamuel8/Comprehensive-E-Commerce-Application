package Project.Panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import Project.Authentication.Collection;
import Project.Main.GUIDesign;

/**
 * UserPanel class handles the user interface and actions for a user in the
 * user panel.
 */
public class UserPanel implements ActionListener {
  public CardLayout card;
  private JButton signupButton, loginButton, findButton, backButtonUser;
  static public JTextField searchBarUser;
  private JPanel userPanel, returnUserPanel;
  public static String userUserName;
  private Collection collection = new Collection();
  private final GUIDesign guiDesign = new GUIDesign();

  /**
   * Constructs a UserPanel and initializes its components.
   */
  public UserPanel() {
    initializeComponents();
    setupPanelLayout();
    addListeners();
  }

  /**
   * Initializes the components for the panel.
   */
  private void initializeComponents() {
    signupButton = new JButton("Signup");
    loginButton = new JButton("Login");
    findButton = new JButton("Find");
    findButton.setActionCommand("UFind");
    backButtonUser = new JButton("← Back");
    searchBarUser = new JTextField(20);
    userPanel = new ImagePanel("UserBack.jpg", new BorderLayout());
  }

  /**
   * Sets up the main panel layout and adds components.
   */
  private void setupPanelLayout() {
    JPanel buttonPanel = createButtonPanel();
    JPanel searchBarPanel = createSearchBarPanel();

    userPanel.add(buttonPanel, BorderLayout.EAST);
    userPanel.add(searchBarPanel, BorderLayout.WEST);
    userPanel.add(createFooterPanel(), BorderLayout.SOUTH);
  }

  /**
   * Creates the button panel with signup, login, and back buttons.
   *
   * @return the button panel
   */
  private JPanel createButtonPanel() {
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(signupButton);
    buttonPanel.add(loginButton);
    buttonPanel.add(backButtonUser);
    buttonPanel.setOpaque(false);
    return buttonPanel;
  }

  /**
   * Creates the search bar panel with search bar and find button.
   *
   * @return the search bar panel
   */
  private JPanel createSearchBarPanel() {
    JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    searchBarPanel.add(searchBarUser);
    searchBarPanel.add(findButton);
    searchBarPanel.setOpaque(false);
    return searchBarPanel;
  }

  /**
   * Creates the footer panel with information.
   *
   * @return the footer panel
   */
  private JPanel createFooterPanel() {
    JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
    footer.add(new JLabel("© 2024 CSE Group Members"));
    footer.add(new JLabel(" | Version 1.0.0"));
    footer.add(new JLabel(" | Contact: https://t.me/ProjectBuddies"));
    footer.setPreferredSize(new Dimension(userPanel.getWidth(), 60));
    footer.setBackground(new Color(4, 7, 32));
    return footer;
  }

  /**
   * Adds action listeners to the components.
   */
  private void addListeners() {
    signupButton.addActionListener(this);
    loginButton.addActionListener(this);
    findButton.addActionListener(this);
    backButtonUser.addActionListener(this);
    searchBarUser.addActionListener(this);
  }

  @Override
  public void actionPerformed(ActionEvent ae) {
    String command = ae.getActionCommand();
    if (command.equals("Login")) {
      boolean isLoggedIn = collection.login();
      if (isLoggedIn) {
        userUserName = collection.retunString();
        card.show(returnUserPanel, "Login");
      }
    } else if (command.equals("Signup")) {
      collection.uSignup();
    } else if (command.equals("UFind")) {
      guiDesign.actionPerformed(ae);
    } else if (command.equals("← Back")) {
      guiDesign.actionPerformed(ae);
    }
  }

  /**
   * Returns the main panel for the user panel view.
   *
   * @return the user panel JPanel
   */
  public JPanel getPanel() {
    card = new CardLayout();
    returnUserPanel = new JPanel(card);
    returnUserPanel.add(userPanel, "userPanel");
    returnUserPanel.add(new UserLoggedInPanel().getPanel(), "Login");
    return returnUserPanel;
  }
}
