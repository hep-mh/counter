import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.*;

import com.formdev.flatlaf.FlatDarculaLaf;

class Counter extends JFrame {

    private JLabel sModeLabel, sDisplayLabel, sTalkLabel, sDiscussionLabel, sPresetLabel;
    private JPanel sControlPanel;
    private JButton sStartButton, sResetButton;
    private JTextField sTalkField, sDiscussionField, sReminderField;
    private JCheckBox sReminderBox;
    private JComboBox sPresetBox;

    private Runner sRunner = null;

    private final Color green  = Color.decode("#4caf50");
    private final Color orange = Color.decode("#ff9800");
    private final Color red    = Color.decode("#f44336");

    //private int sTotalOvertime = 0;

    private class Runner implements Runnable {
        private Thread sThread;
        private int sTalkTimeInSec, sDiscussionTimeInSec, sReminderTimeInSec;
        private int sTimer, sFlag;

        public Runner(int talkTimeInSec, int discussionTimeInSec, int reminderTimeInSec) {
            sTalkTimeInSec = talkTimeInSec;
            sDiscussionTimeInSec = discussionTimeInSec;
            sReminderTimeInSec = reminderTimeInSec;

            sTimer = sTalkTimeInSec + 1;
            sFlag = 0;
        }

        private void playSound() {
            try {
                InputStream audio = getClass().getResourceAsStream("assets/ding.wav");
                InputStream buffer = new BufferedInputStream(audio);

                Clip clip = AudioSystem.getClip();
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(buffer);
                clip.open(audioIn);
                clip.start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        private void startTalk() {
            sDisplayLabel.setForeground(green);
            sModeLabel.setText("Talk");

            sTimer = sTalkTimeInSec + 1;
            sFlag = 0;
        }

        private void startDiscussion() {
            playSound();

            sModeLabel.setText("Discussion");
            sDisplayLabel.setForeground(orange);

            sTimer = sDiscussionTimeInSec + 1;
            sFlag = 1;
        }

        private void startOvertime() {
            playSound();

            sModeLabel.setText("Overtime");
            sDisplayLabel.setForeground(red);

            sTimer = 0;
            sFlag = 2;
        }

        public void run() {
            try {
                startTalk();
                // Loop until the threads gets interrupted
                while(true) {
                    // Switch to discussion once the timer runs out for
                    // the first time
                    if (sTimer == 0 && sFlag == 0) {
                        startDiscussion();
                    }
                    // Switch to overtime once the timer runs out for
                    // the second time
                    if (sTimer == 0 && sFlag == 1) {
                        startOvertime();
                    }
                    // Play a reminder, when there is only a certain amount
                    // of talk time left
                    if (sTimer == sReminderTimeInSec && sFlag == 0) {
                        if (sReminderBox.isSelected()) playSound();
                    }

                    // UPDATE THE TIME ////////////////////////////////////////
                    // For talk/discussion 'count down', for overtime 'count up'
                    sTimer -= (sFlag < 2) ? 1 : -1;

                    // Recalculate the remaining time...
                    int minutes = (int)(sTimer / 60.);
                    int seconds = sTimer - minutes*60;

                    // ... and convert it to a string, thereby potentially
                    // adding the leading zero
                    String minutesAsStr = (minutes > 9) ?
                        Integer.toString(minutes) : "0" + Integer.toString(minutes);
                    String secondsAsStr = (seconds > 9) ?
                        Integer.toString(seconds) : "0" + Integer.toString(seconds);

                    // Display the new time
                    sDisplayLabel.setText(minutesAsStr + ":" + secondsAsStr);

                    // Sleep for 1000ms = 1s
                    Thread.sleep(1000);
                    ///////////////////////////////////////////////////////////
                }
            } catch (InterruptedException e) {}
        }

        public void start() {
            if (sThread == null) {
                sThread = new Thread(this);
                sThread.start();
            }
       }

       public void interrupt() {
           sThread.interrupt();
       }
    }

    private class ButtonListener implements ActionListener {

        private void reset() {
            if (sRunner != null) {
                sRunner.interrupt();
                sRunner = null;
            }

            sModeLabel.setText("Idle");

            int minutes = Integer.parseInt(sTalkField.getText());
            String minutesStr = (minutes > 9) ? Integer.toString(minutes) : "0" + Integer.toString(minutes);

            sDisplayLabel.setText(minutesStr + ":00");
            sDisplayLabel.setForeground(green);
        }

        private void start() {
            if (sRunner == null) {
                reset();

                int talkTimeInSec = Integer.parseInt(sTalkField.getText())*60;
                int discussionTimeInSec = Integer.parseInt(sDiscussionField.getText())*60;
                int reminderTimeInSec = Integer.parseInt(sReminderField.getText())*60;

                sRunner = new Runner(talkTimeInSec, discussionTimeInSec, reminderTimeInSec);
                sRunner.start();
            }
        }

        private void preset() {
            String selectedValue = (String)sPresetBox.getSelectedItem();

            if (selectedValue == "Custom") {
                sTalkField.setEditable(true);
                sDiscussionField.setEditable(true);
                sReminderField.setEditable(true);
            } else { // non-custom preset selected
                sTalkField.setEditable(false);
                sDiscussionField.setEditable(false);
                sReminderField.setEditable(false);

                String[] presetValues = selectedValue.split("\\+");

                sTalkField.setText(presetValues[0]);
                sDiscussionField.setText(presetValues[1]);
                sReminderField.setText(presetValues[1]);

                if (sRunner == null) { reset(); }
            }
        }

        public void actionPerformed(ActionEvent event) {
            if(event.getSource() == sStartButton) {
                start(); // 'Start' button clicked
            } else if(event.getSource() == sResetButton) {
                reset(); // 'Reset' button clicked
            } else if(event.getSource() == sPresetBox) {
                preset() ; // New preset selected
            }
        }
    }

    public Counter() {
        super("Counter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setTitle("Counter");

        // REGISTER FONT //////////////////////////////////////////////////////
        Font roboto = null;
        try {
            roboto = Font.createFont(Font.TRUETYPE_FONT, new File("assets/Roboto-Regular.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
         
            ge.registerFont(roboto);
        } catch (IOException e) {
            e.printStackTrace();
        } catch(FontFormatException e) {
            e.printStackTrace();
        }

        Color backgroundColor = Color.decode("#1f1f1f");
        // SETUP APP LAYOUT ///////////////////////////////////////////////////
        sModeLabel = new JLabel("\nIdle", SwingConstants.CENTER);
        sModeLabel.setBackground(backgroundColor);
        sModeLabel.setForeground(Color.white);
        sModeLabel.setOpaque(true);
        sModeLabel.setFont(roboto.deriveFont(200f));

        sDisplayLabel = new JLabel("12:00", SwingConstants.CENTER);
        sDisplayLabel.setBackground(backgroundColor);
        sDisplayLabel.setForeground(green);
        sDisplayLabel.setOpaque(true);
        sDisplayLabel.setFont(roboto.deriveFont(350f));

        sControlPanel = new JPanel();
        sControlPanel.setBackground(Color.black);

        sTalkLabel = new JLabel("  Talk [min]: ");
        sTalkLabel.setFont(roboto.deriveFont(25f));
        sTalkLabel.setBackground(backgroundColor);
        sTalkLabel.setForeground(Color.white);

        sDiscussionLabel = new JLabel("  Discussion [min]: ");
        sDiscussionLabel.setFont(roboto.deriveFont(25f));
        sDiscussionLabel.setBackground(backgroundColor);
        sDiscussionLabel.setForeground(Color.white);

        sStartButton = new JButton("Start");
        sStartButton.setFont(roboto.deriveFont(25f));
        sStartButton.setOpaque(true);
        sStartButton.setBackground(backgroundColor);
        sStartButton.setForeground(Color.white);
        sStartButton.putClientProperty( "JButton.buttonType", "roundRect" );

        sResetButton = new JButton("Reset");
        sResetButton.setFont(roboto.deriveFont(25f));
        sResetButton.setOpaque(true);
        sResetButton.setBackground(backgroundColor);
        sResetButton.setForeground(Color.white);
        sResetButton.putClientProperty( "JButton.buttonType", "roundRect" );

        sTalkField = new JTextField("12", 3);
        sTalkField.setHorizontalAlignment(SwingConstants.CENTER);
        sTalkField.setFont(roboto.deriveFont(30f));
        sTalkField.setOpaque(true);
        sTalkField.setBackground(backgroundColor);
        sTalkField.setForeground(Color.white);
        sTalkField.putClientProperty( "JComponent.roundRect", true );

        sDiscussionField = new JTextField("3", 3);
        sDiscussionField.setHorizontalAlignment(SwingConstants.CENTER);
        sDiscussionField.setFont(roboto.deriveFont(30f));
        sDiscussionField.setOpaque(true);
        sDiscussionField.setBackground(backgroundColor);
        sDiscussionField.setForeground(Color.white);
        sDiscussionField.putClientProperty( "JComponent.roundRect", true );

        sReminderField = new JTextField("3", 3);
        sReminderField.setHorizontalAlignment(SwingConstants.CENTER);
        sReminderField.setFont(roboto.deriveFont(30f));
        sReminderField.setOpaque(true);
        sReminderField.setBackground(backgroundColor);
        sReminderField.setForeground(Color.white);
        sReminderField.putClientProperty( "JComponent.roundRect", true );

        sReminderBox = new JCheckBox(" Reminder [min] ", true);
        sReminderBox.setHorizontalTextPosition(SwingConstants.LEFT);
        sReminderBox.setFont(roboto.deriveFont(25f));
        sReminderBox.setOpaque(true);
        sReminderBox.setBackground(backgroundColor);
        sReminderBox.setForeground(Color.white);

        sPresetLabel = new JLabel("  Presets: ");
        sPresetLabel.setFont(roboto.deriveFont(25f));
        sPresetLabel.setBackground(backgroundColor);
        sPresetLabel.setForeground(Color.white);

        sPresetBox = new JComboBox(new String[]{
            "Custom", "25+5", "16+4", "12+3", "8+2"
        });
        sPresetBox.setFont(roboto.deriveFont(25f));
        sPresetBox.setOpaque(true);
        sPresetBox.setBackground(backgroundColor);
        sPresetBox.setForeground(Color.white);
        sPresetBox.putClientProperty( "JComponent.roundRect", true );

        sControlPanel.add(sStartButton);
        sControlPanel.add(sResetButton);
        sControlPanel.add(sTalkLabel);
        sControlPanel.add(sTalkField);
        sControlPanel.add(sDiscussionLabel);
        sControlPanel.add(sDiscussionField);
        sControlPanel.add(sReminderBox);
        sControlPanel.add(sReminderField);
        sControlPanel.add(sPresetLabel);
        sControlPanel.add(sPresetBox);
        sControlPanel.setBackground(backgroundColor);

        sStartButton.addActionListener(new ButtonListener());
        sResetButton.addActionListener(new ButtonListener());
        sPresetBox.addActionListener(new ButtonListener());

        getContentPane().add(BorderLayout.PAGE_START, sModeLabel);
        getContentPane().add(BorderLayout.CENTER, sDisplayLabel);
        getContentPane().add(BorderLayout.PAGE_END, sControlPanel);
        ///////////////////////////////////////////////////////////////////////
    }
}

class Main {
    public static void main(String args[]) {
        // Set Look & Feel
        FlatDarculaLaf.setup();

        Counter sCounter = new Counter();
        //sCounter.setExtendedState(JFrame.MAXIMIZED_BOTH);
        sCounter.setMinimumSize(new Dimension(1400, 800));
        sCounter.setVisible(true);
     }
}
