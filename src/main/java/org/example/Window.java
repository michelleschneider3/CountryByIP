package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class Window extends JFrame {
    public Window () {
        this.setBounds(0,0,Constants.WINDOW_SIZE,Constants.WINDOW_SIZE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setTitle("IP Tracker");
        this.setLayout(null);
        this.getContentPane().setBackground(new Color(62, 189, 185, 255));
        this.setLocationRelativeTo(null);


        JLabel ipLabel = new JLabel("Write your IP address or domain");
        ipLabel.setBounds((Constants.WINDOW_SIZE-Constants.LABEL_WIDTH)/2, (Constants.WINDOW_SIZE-2*(Constants.LABEL_HEIGHT+Constants.TEXT_FIELD_HEIGHT+Constants.MARGIN_TOP))/2-Constants.LABEL_HEIGHT/2, Constants.LABEL_WIDTH, Constants.LABEL_HEIGHT);
        Font ipFont = new Font("Comic Sans MS", Font.BOLD, Constants.LABEL_FONT_SIZE);
        ipLabel.setFont(ipFont);
        this.add(ipLabel);

        JTextField searchTextField = new JTextField();
        searchTextField.setBounds((Constants.WINDOW_SIZE-Constants.TEXT_FIELD_WIDTH-Constants.BUTTON_WIDTH-Constants.MARGIN_LEFT)/2, ipLabel.getY() + Constants.LABEL_HEIGHT + Constants.MARGIN_TOP, Constants.TEXT_FIELD_WIDTH, Constants.TEXT_FIELD_HEIGHT);
        searchTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK, Constants.BUTTON_BORDERS_THICKNESS));
        this.add(searchTextField);

        JButton searchButton = new JButton("Search");
        searchButton.setBounds(searchTextField.getX() + Constants.TEXT_FIELD_WIDTH + Constants.MARGIN_LEFT, searchTextField.getY(), Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
        searchButton.setBackground(new Color(255, 255, 255, 255));
        searchButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, Constants.BUTTON_BORDERS_THICKNESS));
        Font searchButtonFont = new Font("Comic Sans MS", Font.BOLD, Constants.BUTTON_FONT_SIZE);
        searchButton.setFont(searchButtonFont);
        searchButton.setFocusable(false);
        this.add(searchButton);

        JLabel resultLabel = new JLabel();
        resultLabel.setBounds(0,0,0,0);
        resultLabel.setVisible(false);
        this.add(resultLabel);

        searchButton.addActionListener(e -> {
            String userInput = searchTextField.getText().trim();
            String url = "https://tools.keycdn.com/geo?host=" + userInput;

            String output = "";
            if (isValidIPAddress(userInput) || isValidDomain(userInput)) {
                try {
                    Document document = Jsoup.connect(url).get();
                    List<Element> elements = document.getElementsByClass("row");
                    int i = 0;
                    boolean hasFound = false;
                    while (i<elements.get(Constants.DATA_ROW_INDEX).children().size()) {
                        if (elements.get(Constants.DATA_ROW_INDEX).children().get(i).text().trim().equals("Country")) {
                            i++;
                            hasFound = true;
                            break;
                        }
                        i++;
                    }
                    if (hasFound) {
                        output = "Your country is " + elements.get(Constants.DATA_ROW_INDEX).children().get(i).text().trim();
                    } else {
                        output = "The input doesn't exist";
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                output = "Incorrect input. Try again";
            }
            resultLabel.setText(output);
            int resultLabelWidth = resultLabel.getPreferredSize().width + Constants.PADDING_RIGHT;
            resultLabel.setBounds((Constants.WINDOW_SIZE-resultLabelWidth)/2, searchTextField.getY() + Constants.LABEL_HEIGHT + Constants.MARGIN_TOP, resultLabelWidth, Constants.LABEL_HEIGHT);
            resultLabel.setVisible(true);
        });

        this.setVisible(true);


    }

    private boolean isValidIPAddress (String ip) {
        String[] parts = ip.split("\\.");
        boolean isValid = parts.length==Constants.CORRECT_IP_PARTS;
        if (isValid) {
            for (String part : parts) {
                try {
                    int value = Integer.parseInt(part);
                    if (value<0 || value>Constants.MAX_IP_PART_VALUE) {
                        isValid = false;
                        break;
                    }
                } catch (NumberFormatException e) {
                    isValid = false;
                    break;
                }
            }
        }
        return isValid;
    }

    private boolean isValidDomain (String domain) {
        String[] parts = domain.split("\\.");
        boolean isValid = parts.length>=Constants.MINIMUM_DOMAIN_PARTS;
        if (isValid) {
            for (String part : parts) {
                if (!isValidDomainPart(part)) {
                    isValid = false;
                    break;
                }
            }
        }
        return isValid;
    }

    private boolean isValidDomainPart (String part) {
        boolean isValid = part.length()>=1 && part.length()<=Constants.MAXIMUM_DOMAIN_PART_LENGTH;
        if (isValid && !part.startsWith("-") && !part.endsWith("-")) {
            for (int i = 0; i < part.length(); i++) {
                char c = part.charAt(i);
                if (!Character.isLetterOrDigit(c) && c!='-') {
                    isValid = false;
                    break;
                }
            }
        }
        return isValid;
    }

}
