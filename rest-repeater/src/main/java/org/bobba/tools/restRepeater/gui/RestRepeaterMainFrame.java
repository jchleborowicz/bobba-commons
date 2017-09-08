package org.bobba.tools.restRepeater.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;

public class RestRepeaterMainFrame extends JFrame {

    private final JTextField hostTextField = new JTextField();
    private final JTextField portTextField = new JTextField();

    public RestRepeaterMainFrame() throws HeadlessException {
        super("REST Repeater");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void initAndShow() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            init();
            pack();
            setVisible(true);
        });
    }

    private void init() {
        final JPanel criteriaPanel = createCriteriaPanel();
        final JPanel operationPanel = createOperationPanel();

        setLayout(new BorderLayout());
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        criteriaPanel.setLayout(new GridBagLayout());
        criteriaPanel.setSize(new Dimension(300, 1300));
        final GridBagConstraints constraints = new GridBagConstraints();
//        constraints.weightx = 0.5;
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 0;
        criteriaPanel.add(new JLabel("Host"));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 1;
        criteriaPanel.add(hostTextField);
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 1;
        constraints.gridy = 0;
        criteriaPanel.add(new JLabel("Port"));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 1;
        criteriaPanel.add(portTextField);

        operationPanel.setLayout(new BorderLayout());
        operationPanel.add(new JScrollPane(new JTextArea()), BorderLayout.NORTH);
        operationPanel.add(new JScrollPane(new JTextArea()), BorderLayout.CENTER);

        contentPane.add(criteriaPanel, BorderLayout.WEST);
        contentPane.add(operationPanel, BorderLayout.CENTER);
    }

    private JPanel createOperationPanel() {
        return new JPanel();
    }

    private JPanel createCriteriaPanel() {
        return createOperationPanel();
    }


}
