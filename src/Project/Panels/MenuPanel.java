package Project.Panels;

import javax.swing.*;
import java.sql.*;
import Project.Main.GUIDesign;
import java.awt.*;
import java.awt.event.*;

/**
 * MenuPanel class creates a panel with a menu bar containing update and delete
 * options for an item.
 */
public class MenuPanel {
  JMenuBar menuBar;
  JMenuItem delete;
  JMenuItem update;
  JMenu menu;
  JPanel menuPanel;
  private int itemID;
  private double price;
  private String name, detail;
  private static boolean deleted = false;

  /**
   * Constructs a MenuPanel for a specific item.
   *
   * @param itemID the ID of the item
   * @param name   the name of the item
   * @param price  the price of the item
   * @param detail the details of the item
   */
  public MenuPanel(int itemID, String name, double price, String detail) {
    this.name = name;
    this.price = price;
    this.detail = detail;
    this.itemID = itemID;
    menuBar = new JMenuBar();

    // Create menu and menu items
    menu = new JMenu("...");
    update = new JMenuItem("Update");
    delete = new JMenuItem("Delete");

    // Add menu items to menu
    menu.add(update);
    menu.add(delete);

    // Add menu to menu bar
    menuBar.add(menu);

    // Create panel and add menu bar
    menuPanel = new JPanel(new BorderLayout());
    menuPanel.add(menuBar, BorderLayout.EAST);

    // Set up actions for menu items
    action();
  }

  /**
   * Returns the deleted state.
   *
   * @return true if the item has been deleted, otherwise false
   */
  public static boolean getstate() {
    return deleted;
  }

  /**
   * Sets up actions for the update and delete menu items.
   */
  private void action() {
    update.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        new UpdateFrame(itemID, name, price, detail);
      }
    });

    delete.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        String useDB = "USE Project";
        String sql = "DELETE FROM item WHERE itemid = ?";
        try (Statement st = GUIDesign.connection.createStatement()) {
          st.execute(useDB); // Execute the USE statement separately
          try (PreparedStatement pst = GUIDesign.connection.prepareStatement(sql)) {
            pst.setInt(1, itemID); // Set the itemID parameter
            pst.executeUpdate();
            deleted = true;
            JOptionPane.showMessageDialog(null, "Deleted Successfully!");
          }
        } catch (SQLException e) {
          JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });
  }

  /**
   * Returns the panel containing the menu bar.
   *
   * @return the JPanel containing the menu bar
   */
  public JPanel getBar() {
    return menuPanel;
  }
}

/**
 * UpdateFrame class creates a frame to update an item's details.
 */
class UpdateFrame {
  private static boolean deleted;

  /**
   * Constructs an UpdateFrame for a specific item.
   *
   * @param itemID the ID of the item
   * @param name   the name of the item
   * @param price  the price of the item
   * @param detail the details of the item
   */
  public UpdateFrame(int itemID, String name, double price, String detail) {
    String useDB = "USE Project";
    String sql = "DELETE FROM item WHERE itemid = ?";
    try (Statement st = GUIDesign.connection.createStatement()) {
      st.execute(useDB); // Execute the USE statement separately
      try (PreparedStatement pst = GUIDesign.connection.prepareStatement(sql)) {
        pst.setInt(1, itemID); // Set the itemID parameter
        pst.executeUpdate();
        deleted = true;
      }
    } catch (SQLException e) {
      deleted = false;
      // JOptionPane.showMessageDialog(null,"Unable To Update");
    }
    if (deleted) {
      // Create frame for updating item details
      JFrame frame = new JFrame();
      frame.setTitle("Update Category");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(700, 400);
      frame.add(new PostPanel(GUIDesign.connection, CompanyPanel.returnedString, name, price, detail).getPanel());
      frame.setVisible(true);
    } else {
      JOptionPane.showMessageDialog(null, "Cannot process try again!");
    }
  }
}
