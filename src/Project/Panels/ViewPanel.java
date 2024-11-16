package Project.Panels;

import javax.swing.*;
import javax.swing.border.Border;
import Project.Authentication.SQLConnection;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;

/**
 * ViewPanel class displays items and their details, including an option to
 * update or delete items.
 */
public class ViewPanel {
  private JPanel resultPanel;
  private ResultSet dbHelper;
  Border border = BorderFactory.createLineBorder(Color.WHITE, 5);

  /**
   * Constructs a ViewPanel and initializes its components.
   */
  public ViewPanel() {
    dbHelper = new SQLConnection().getItemData(CompanyPanel.returnedString);
    resultPanel = new JPanel();
    resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
    displayItems(CompanyPanel.returnedString);
    System.out.println(CompanyPanel.returnedString);
    if (MenuPanel.getstate()) {
      refreshItems(CompanyPanel.returnedString);
    }
  }

  /**
   * Displays items for the specified input (company name).
   *
   * @param input the company name to fetch items for
   */
  private void displayItems(String input) {
    Map<Integer, Item> itemMap = new HashMap<>();
    dbHelper = new SQLConnection().getItemData(input);

    if (dbHelper == null) {
      // Handle case where dbHelper is null
    } else {
      try (ResultSet rs = dbHelper) {
        while (rs.next()) {
          int itemId = rs.getInt("itemId");
          String name = rs.getString("itemName");
          double price = rs.getDouble("itemPrice");
          String detail = rs.getString("itemDetail");
          byte[] imageBytes = rs.getBytes("picture");

          ImageIcon imageIcon = new ImageIcon(imageBytes);

          if (itemMap.containsKey(itemId)) {
            itemMap.get(itemId).addImage(imageIcon);
          } else {
            Item item = new Item(itemId, name, price, detail);
            item.addImage(imageIcon);
            itemMap.put(itemId, item);
          }
        }

        for (Item item : itemMap.values()) {
          JPanel itemPanel = new JPanel();
          itemPanel.setLayout(new BorderLayout());

          JPanel imagesPanel = new JPanel();
          imagesPanel.setLayout(new FlowLayout());
          for (ImageIcon imageIcon : item.getImages()) {
            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setPreferredSize(new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight()));
            ImageIcon icon = (ImageIcon) imageLabel.getIcon();
            Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(image));
            imageLabel.setPreferredSize(new Dimension(150, 150));
            imagesPanel.add(imageLabel);
          }

          JLabel nameLabel = new JLabel(item.getName() + " - $" + item.getPrice());

          itemPanel.add(imagesPanel, BorderLayout.WEST);
          itemPanel.add(nameLabel, BorderLayout.SOUTH);
          itemPanel.add(new MenuPanel(item.getId(), item.getName(), item.getPrice(), item.getDetail()).getBar(),
              BorderLayout.EAST);
          itemPanel.setBorder(border);

          itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
              showItemDetail(item.getName(), item.getPrice(), item.getDetail());
            }
          });

          resultPanel.add(itemPanel);
        }
      } catch (SQLException e) {
        System.out.println("Error: " + e.getMessage());
      }
    }
  }

  /**
   * Shows the details of the specified item.
   *
   * @param name   the name of the item
   * @param price  the price of the item
   * @param detail the details of the item
   */
  private void showItemDetail(String name, double price, String detail) {
    // Implement the method to show item details
  }

  /**
   * Returns the result panel containing the items.
   *
   * @return the result panel
   */
  public JPanel getResultPanel() {
    JButton refreshButton = new JButton("Refresh");
    refreshButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        refreshItems(CompanyPanel.returnedString);
      }
    });
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(refreshButton, BorderLayout.NORTH);
    panel.add(new JScrollPane(resultPanel), BorderLayout.CENTER);

    return panel;
  }

  /**
   * Refreshes the items displayed in the panel.
   *
   * @param input the company name to fetch items for
   */
  public void refreshItems(String input) {
    resultPanel.removeAll();
    displayItems(input);
    resultPanel.revalidate();
    resultPanel.repaint();
  }
}

/**
 * Item class represents an item with details and images.
 */
class Item {
  private int id;
  private String name;
  private double price;
  private String detail;
  private List<ImageIcon> images;

  /**
   * Constructs an Item with the specified details.
   *
   * @param id     the ID of the item
   * @param name   the name of the item
   * @param price  the price of the item
   * @param detail the details of the item
   */
  public Item(int id, String name, double price, String detail) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.detail = detail;
    this.images = new ArrayList<>();
  }

  /**
   * Adds an image to the item's image list.
   *
   * @param image the ImageIcon to add
   */
  public void addImage(ImageIcon image) {
    images.add(image);
  }

  /**
   * Gets the list of images for the item.
   *
   * @return the list of ImageIcons
   */
  public List<ImageIcon> getImages() {
    return images;
  }

  /**
   * Gets the name of the item.
   *
   * @return the name of the item
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the price of the item.
   *
   * @return the price of the item
   */
  public double getPrice() {
    return price;
  }

  /**
   * Gets the details of the item.
   *
   * @return the details of the item
   */
  public String getDetail() {
    return detail;
  }

  /**
   * Gets the ID of the item.
   *
   * @return the ID of the item
   */
  protected Integer getId() {
    return id;
  }
}
