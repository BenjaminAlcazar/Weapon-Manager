package inventoryui;

import javax.swing.*;

import inventory.InventorySaver;
import inventory.Weapon;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.io.PrintWriter;

public class InventoryFrame extends JFrame {

    private JList<Weapon> weaponList;
    private DefaultListModel<Weapon> weaponlist;
    private List<Weapon> weapons;
    private JTextField searchField;
    private JButton searchButton;

    public InventoryFrame() {
        setTitle("Game Inventory Manager");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        weapons = InventorySaver.loadWeapons();

        weaponlist = new DefaultListModel<>();
        for (Weapon w : weapons) weaponlist.addElement(w);

        weaponList = new JList<>(weaponlist);
        JPanel centerPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(weaponList);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel detailsPanel = new JPanel(new GridLayout(0, 1));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Weapon Details"));

        JLabel nameLabel = new JLabel("Name: ");
        JLabel typeLabel = new JLabel("Type: ");
        JLabel damageLabel = new JLabel("Damage: ");
        JLabel rarityLabel = new JLabel("Rarity: ");
        JTextArea tagsArea = new JTextArea();
        tagsArea.setLineWrap(true);
        tagsArea.setWrapStyleWord(true);
        tagsArea.setEditable(false);
        JScrollPane tagsScroll = new JScrollPane(tagsArea);

        detailsPanel.add(nameLabel);
        detailsPanel.add(typeLabel);
        detailsPanel.add(damageLabel);
        detailsPanel.add(rarityLabel);
        detailsPanel.add(new JLabel("Tags:"));
        detailsPanel.add(tagsScroll);

        centerPanel.add(detailsPanel, BorderLayout.EAST);

        weaponList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Weapon selected = weaponList.getSelectedValue();
                if (selected != null) {
                    nameLabel.setText("Name: " + selected.getName());
                    typeLabel.setText("Type: " + selected.getType());
                    damageLabel.setText("Damage: " + selected.getDamage());
                    rarityLabel.setText("Rarity: " + selected.getRarity());
                    tagsArea.setText(String.join(", ", selected.getTags()));
                } else {
                    nameLabel.setText("Name: ");
                    typeLabel.setText("Type: ");
                    damageLabel.setText("Damage: ");
                    rarityLabel.setText("Rarity: ");
                    tagsArea.setText("");
                }
            }
        });

        add(centerPanel, BorderLayout.CENTER);
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        //search bar stuff
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchWeapons());
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        //Add stuff
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {

            JTextField nameField = new JTextField(10);
            JTextField damageField = new JTextField(5);

            JRadioButton meleeBtn = new JRadioButton("MELEE");
            JRadioButton rangedBtn = new JRadioButton("RANGED");
            JRadioButton magicBtn = new JRadioButton("MAGIC");
            ButtonGroup typeGroup = new ButtonGroup();
            typeGroup.add(meleeBtn); typeGroup.add(rangedBtn); typeGroup.add(magicBtn);
            meleeBtn.setSelected(true);

            JRadioButton commonBtn = new JRadioButton("COMMON");
            JRadioButton rareBtn = new JRadioButton("RARE");
            JRadioButton epicBtn = new JRadioButton("EPIC");
            ButtonGroup rarityGroup = new ButtonGroup();
            rarityGroup.add(commonBtn); rarityGroup.add(rareBtn); rarityGroup.add(epicBtn);
            commonBtn.setSelected(true);

            JTextField tagsField = new JTextField(15);
            tagsField.setToolTipText("Enter tags separated by commas");

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Weapon Name:")); panel.add(nameField);
            panel.add(new JLabel("Weapon Type:")); panel.add(meleeBtn); panel.add(rangedBtn); panel.add(magicBtn);
            panel.add(new JLabel("Weapon Rarity:")); panel.add(commonBtn); panel.add(rareBtn); panel.add(epicBtn);
            panel.add(new JLabel("Damage:")); panel.add(damageField);
            panel.add(new JLabel("Tags (comma-separated):")); panel.add(tagsField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Add New Weapon", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            String name = nameField.getText();
            if (name.isEmpty()) return;

            String type = meleeBtn.isSelected() ? "MELEE" : rangedBtn.isSelected() ? "RANGED" : "MAGIC";
            String rarity = commonBtn.isSelected() ? "COMMON" : rareBtn.isSelected() ? "RARE" : "EPIC";

            int damage;
            try { damage = Integer.parseInt(damageField.getText()); } catch (Exception ex) { return; }
            Weapon newWeapon = new Weapon(name, type, damage, rarity);
            String[] tagsArray = tagsField.getText().split(",");
            for (String t : tagsArray) {
                t = t.trim();
                if (!t.isEmpty()) newWeapon.addTag(t);
            }

            weapons.add(newWeapon);
            weaponlist.addElement(newWeapon);
        });
        buttonPanel.add(addButton);

        //edit stff
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> {

            int index = weaponList.getSelectedIndex();
            if (index == -1) return;

            Weapon selected = weapons.get(index);

            JTextField nameField = new JTextField(selected.getName(), 10);
            JTextField damageField = new JTextField(String.valueOf(selected.getDamage()), 5);

            JRadioButton meleeBtn = new JRadioButton("MELEE");
            JRadioButton rangedBtn = new JRadioButton("RANGED");
            JRadioButton magicBtn = new JRadioButton("MAGIC");
            ButtonGroup typeGroup = new ButtonGroup();
            typeGroup.add(meleeBtn); typeGroup.add(rangedBtn); typeGroup.add(magicBtn);
            switch(selected.getType().toUpperCase()) {
                case "MELEE": meleeBtn.setSelected(true); break;
                case "RANGED": rangedBtn.setSelected(true); break;
                case "MAGIC": magicBtn.setSelected(true); break;
                default: meleeBtn.setSelected(true);
            }

            JRadioButton commonBtn = new JRadioButton("COMMON");
            JRadioButton rareBtn = new JRadioButton("RARE");
            JRadioButton epicBtn = new JRadioButton("EPIC");
            ButtonGroup rarityGroup = new ButtonGroup();
            rarityGroup.add(commonBtn); rarityGroup.add(rareBtn); rarityGroup.add(epicBtn);
            switch(selected.getRarity().toUpperCase()) {
                case "COMMON": commonBtn.setSelected(true); break;
                case "RARE": rareBtn.setSelected(true); break;
                case "EPIC": epicBtn.setSelected(true); break;
                default: commonBtn.setSelected(true);
            }

            String tagsText = String.join(", ", selected.getTags());
            JTextField tagsField = new JTextField(tagsText, 15);
            tagsField.setToolTipText("Enter tags separated by commas");

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Weapon Name:")); panel.add(nameField);
            panel.add(new JLabel("Weapon Type:")); panel.add(meleeBtn); panel.add(rangedBtn); panel.add(magicBtn);
            panel.add(new JLabel("Weapon Rarity:")); panel.add(commonBtn); panel.add(rareBtn); panel.add(epicBtn);
            panel.add(new JLabel("Damage:")); panel.add(damageField);
            panel.add(new JLabel("Tags (comma-separated):")); panel.add(tagsField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Edit Weapon", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            String name = nameField.getText(); if (name.isEmpty()) return;
            String type = meleeBtn.isSelected() ? "MELEE" : rangedBtn.isSelected() ? "RANGED" : "MAGIC";
            String rarity = commonBtn.isSelected() ? "COMMON" : rareBtn.isSelected() ? "RARE" : "EPIC";

            int damage;
            try { damage = Integer.parseInt(damageField.getText()); } catch (Exception ex) { return; }

            selected.setName(name); selected.setType(type); selected.setRarity(rarity); selected.setDamage(damage);

            selected.getTags().clear();
            String[] tagsArray = tagsField.getText().split(",");
            for (String t : tagsArray) {
                t = t.trim();
                if (!t.isEmpty()) selected.addTag(t);
            }

            weaponlist.set(index, selected);
        });
        buttonPanel.add(editButton);	

        //Remove stuff
        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> {
            int index = weaponList.getSelectedIndex();
            if (index == -1) return;

            Weapon selected = weaponlist.get(index);
            weapons.remove(selected);
            weaponlist.remove(index);
        });
        buttonPanel.add(removeButton);

        //Ssave stuff
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> InventorySaver.saveWeapons(weapons));
        buttonPanel.add(saveButton);
        
        //Make note copy
        JButton exportButton = new JButton("Export Inventory (CSV)");
        exportButton.addActionListener(e -> {
            try (PrintWriter writer = new PrintWriter("inventory_export.csv")) {
                writer.println("Name,Type,Damage,Rarity,Tags");
                for (Weapon w : weapons) {
                    String tags = String.join("|", w.getTags()); // Use | to separate tags
                    writer.printf("%s,%s,%d,%s,%s%n",
                            w.getName(),
                            w.getType(),
                            w.getDamage(),
                            w.getRarity(),
                            tags);
                }
                JOptionPane.showMessageDialog(this, "Inventory exported to inventory_export.csv\nCan be found in Project Folder Location");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error exporting inventory: " + ex.getMessage());
            }
        });
        buttonPanel.add(exportButton);
        
        //Sort stuff
        JButton sortButton = new JButton("Sort");
        sortButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JPanel panel = new JPanel(new GridLayout(0, 1));
                JButton nameBtn = new JButton("Name");
                JButton typeBtn = new JButton("Type");
                JButton damageBtn = new JButton("Damage");
                JButton rarityBtn = new JButton("Rarity");

                panel.add(new JLabel("Sort by:"));
                panel.add(nameBtn);
                panel.add(typeBtn);
                panel.add(damageBtn);
                panel.add(rarityBtn);

                JDialog dialog = new JDialog(InventoryFrame.this, "Sort Weapons", true);
                dialog.setContentPane(panel);
                dialog.pack();
                dialog.setLocationRelativeTo(InventoryFrame.this);

                ActionListener sortListener = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String criteria = ((JButton)e.getSource()).getText().toLowerCase();

                        switch (criteria) {
                            case "name":
                                weapons.sort((w1, w2) -> w1.getName().compareToIgnoreCase(w2.getName()));
                                break;
                            case "type":
                                weapons.sort((w1, w2) -> w1.getType().compareToIgnoreCase(w2.getType()));
                                break;
                            case "damage":
                                weapons.sort((w1, w2) -> Integer.compare(w2.getDamage(), w1.getDamage()));
                                break;
                            case "rarity":
                                weapons.sort((w1, w2) -> w1.getRarity().compareToIgnoreCase(w2.getRarity()));
                                break;
                        }

                        weaponlist.clear();
                        for (Weapon w : weapons) weaponlist.addElement(w);
                        dialog.dispose();
                    }
                };

                nameBtn.addActionListener(sortListener);
                typeBtn.addActionListener(sortListener);
                damageBtn.addActionListener(sortListener);
                rarityBtn.addActionListener(sortListener);

                dialog.setVisible(true);
            }
        });
        buttonPanel.add(sortButton);

        //Filter stuff
        JButton filterButton = new JButton("Filter");
        filterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JPanel mainPanel = new JPanel(new GridLayout(0, 1));
                JButton typeBtn = new JButton("Type");
                JButton rarityBtn = new JButton("Rarity");

                mainPanel.add(new JLabel("Filter by:"));
                mainPanel.add(typeBtn);
                mainPanel.add(rarityBtn);

                JDialog filterDialog = new JDialog(InventoryFrame.this, "Filter Weapons", true);
                filterDialog.setContentPane(mainPanel);
                filterDialog.pack();
                filterDialog.setLocationRelativeTo(InventoryFrame.this);

                typeBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ev) {
                        JPanel typePanel = new JPanel(new GridLayout(0, 1));
                        JButton meleeBtn = new JButton("MELEE");
                        JButton rangedBtn = new JButton("RANGED");
                        JButton magicBtn = new JButton("MAGIC");
                        typePanel.add(new JLabel("Filter by Type:"));
                        typePanel.add(meleeBtn);
                        typePanel.add(rangedBtn);
                        typePanel.add(magicBtn);

                        JDialog typeDialog = new JDialog(InventoryFrame.this, "Select Type", true);
                        typeDialog.setContentPane(typePanel);
                        typeDialog.pack();
                        typeDialog.setLocationRelativeTo(InventoryFrame.this);

                        ActionListener typeFilter = new ActionListener() {
                            public void actionPerformed(ActionEvent e2) {
                                String value = ((JButton)e2.getSource()).getText();
                                weaponlist.clear();
                                for (Weapon w : weapons) {
                                    if (w.getType().equalsIgnoreCase(value)) weaponlist.addElement(w);
                                }
                                typeDialog.dispose();
                                filterDialog.dispose();
                            }
                        };

                        meleeBtn.addActionListener(typeFilter);
                        rangedBtn.addActionListener(typeFilter);
                        magicBtn.addActionListener(typeFilter);

                        typeDialog.setVisible(true);
                    }
                });

                rarityBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ev) {
                        JPanel rarityPanel = new JPanel(new GridLayout(0, 1));
                        JButton commonBtn = new JButton("COMMON");
                        JButton rareBtn = new JButton("RARE");
                        JButton epicBtn = new JButton("EPIC");
                        rarityPanel.add(new JLabel("Filter by Rarity:"));
                        rarityPanel.add(commonBtn);
                        rarityPanel.add(rareBtn);
                        rarityPanel.add(epicBtn);

                        JDialog rarityDialog = new JDialog(InventoryFrame.this, "Select Rarity", true);
                        rarityDialog.setContentPane(rarityPanel);
                        rarityDialog.pack();
                        rarityDialog.setLocationRelativeTo(InventoryFrame.this);

                        ActionListener rarityFilter = new ActionListener() {
                            public void actionPerformed(ActionEvent e2) {
                                String value = ((JButton)e2.getSource()).getText();
                                weaponlist.clear();
                                for (Weapon w : weapons) {
                                    if (w.getRarity().equalsIgnoreCase(value)) weaponlist.addElement(w);
                                }
                                rarityDialog.dispose();
                                filterDialog.dispose();
                            }
                        };

                        commonBtn.addActionListener(rarityFilter);
                        rareBtn.addActionListener(rarityFilter);
                        epicBtn.addActionListener(rarityFilter);

                        rarityDialog.setVisible(true);
                    }
                });

                filterDialog.setVisible(true);
            }
        });
        buttonPanel.add(filterButton);
        //Reset
        JButton resetButton = new JButton("Reset Filter");
        resetButton.addActionListener(e -> {
            weaponlist.clear();
            for (Weapon w : weapons) weaponlist.addElement(w);
        });
        buttonPanel.add(resetButton);
        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }
    
  //searching search lol
    private void searchWeapons() {
    	String text = searchField.getText().trim().toLowerCase();
        if (text.isEmpty()) return;

        weaponlist.clear();

        for (Weapon w : weapons) {
            boolean matchesName = w.getName().toLowerCase().contains(text);
            boolean matchesTag = w.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(text));
            if (matchesName || matchesTag) {
                weaponlist.addElement(w);
            }
        }
    }
    
}

