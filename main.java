import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.*;

class Counter extends JFrame {

    private JLabel sModeLabel, sDisplayLabel, sTalkLabel, sDiscussionLabel, sPresetLabel;
    private JPanel sControlPanel;
    private JButton sStartButton, sResetButton;
    private JTextField sTalkField, sDiscussionField, sReminderField;
    private JCheckBox sReminderBox;
    private JComboBox sPresetBox;

    private Runner sRunner = null;

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

        public void playSound() {
            try {
                InputStream audio = getClass().getResourceAsStream("res/ding.wav");
                InputStream buffer = new BufferedInputStream(audio);

                Clip clip = AudioSystem.getClip();
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(buffer);
                clip.open(audioIn);
                clip.start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        public void startTalk() {
            sDisplayLabel.setForeground(Color.green);
            sModeLabel.setText("Talk");

            sTimer = sTalkTimeInSec + 1;
            sFlag = 0;
        }

        public void startDiscussion() {
            playSound();

            sModeLabel.setText("Discussion");
            sDisplayLabel.setForeground(Color.yellow);

            sTimer = sDiscussionTimeInSec + 1;
            sFlag = 1;
        }

        public void startOvertime() {
            playSound();

            sModeLabel.setText("Overtime");
            sDisplayLabel.setForeground(Color.red);

            sTimer = 0;
            sFlag = 2;
        }

        public void run() {
            startTalk();

            try {
                while(true) {
                    // Switch to discussion once the timer runs out for the first time
                    if (sTimer == 0 && sFlag == 0) {
                        startDiscussion();
                    }
                    // Switch to overtime once the timer runs out for the second time
                    if (sTimer == 0 && sFlag == 1) {
                        startOvertime();
                    }
                    // Play reminder if the checkbox is checked
                    if (sTimer == sReminderTimeInSec && sFlag == 0 && sReminderBox.isSelected()) {
                        playSound();
                    }

                    // For talk and discussion 'count down', for overtime 'count up'
                    sTimer -= (sFlag < 2) ? 1 : -1;

                    // Recalculate the remaining time...
                    int minutes = (int)(sTimer / 60.);
                    int seconds = sTimer - minutes*60;

                    // ... and onvert it to  string (+ add leading zeros if necessary)
                    String minutesAsStr = (minutes > 9) ?
                        Integer.toString(minutes) : "0" + Integer.toString(minutes);
                    String secondsAsStr = (seconds > 9) ?
                        Integer.toString(seconds) : "0" + Integer.toString(seconds);

                    // Display the new time
                    sDisplayLabel.setText(minutesAsStr + ":" + secondsAsStr);

                    // Sleep for 1000ms = 1s
                    Thread.sleep(1000);
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
            sDisplayLabel.setForeground(Color.green);
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

                String[] presetValues = selectedValue.split("-");

                sTalkField.setText(presetValues[0]);
                sDiscussionField.setText(presetValues[1]);
                sReminderField.setText(presetValues[2]);

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
        setSize(1500, 1000);
        setTitle("Counter");

        // SETUP APP LAYOUT ///////////////////////////////////////////////////
        sModeLabel = new JLabel("\nIdle", SwingConstants.CENTER);
        sModeLabel.setBackground(Color.black);
        sModeLabel.setForeground(Color.white);
        sModeLabel.setOpaque(true);
        sModeLabel.setFont(new Font("Serif", Font.PLAIN, 200));

        sDisplayLabel = new JLabel("12:00", SwingConstants.CENTER);
        sDisplayLabel.setBackground(Color.black);
        sDisplayLabel.setForeground(Color.green);
        sDisplayLabel.setOpaque(true);
        sDisplayLabel.setFont(new Font("Serif", Font.PLAIN, 350));

        sControlPanel = new JPanel();
        sControlPanel.setBackground(Color.black);

        sTalkLabel = new JLabel("  Talk [min]: ");
        sTalkLabel.setFont(new Font("Serif", Font.PLAIN, 25));
        sTalkLabel.setBackground(Color.black);
        sTalkLabel.setForeground(Color.white);

        sDiscussionLabel = new JLabel("  Discussion [min]: ");
        sDiscussionLabel.setFont(new Font("Serif", Font.PLAIN, 25));
        sDiscussionLabel.setBackground(Color.black);
        sDiscussionLabel.setForeground(Color.white);

        sStartButton = new JButton("Start");
        sStartButton.setFont(new Font("Sans Serif", Font.PLAIN, 25));
        sStartButton.setOpaque(true);
        sStartButton.setBackground(Color.black);
        sStartButton.setForeground(Color.white);

        sResetButton = new JButton("Reset");
        sResetButton.setFont(new Font("Sans Serif", Font.PLAIN, 25));
        sResetButton.setOpaque(true);
        sResetButton.setBackground(Color.black);
        sResetButton.setForeground(Color.white);

        sTalkField = new JTextField("12", 3);
        sTalkField.setHorizontalAlignment(SwingConstants.CENTER);
        sTalkField.setFont(new Font("Serif", Font.PLAIN, 30));
        sTalkField.setOpaque(true);
        sTalkField.setBackground(Color.black);
        sTalkField.setForeground(Color.white);

        sDiscussionField = new JTextField("3", 3);
        sDiscussionField.setHorizontalAlignment(SwingConstants.CENTER);
        sDiscussionField.setFont(new Font("Serif", Font.PLAIN, 30));
        sDiscussionField.setOpaque(true);
        sDiscussionField.setBackground(Color.black);
        sDiscussionField.setForeground(Color.white);

        sReminderField = new JTextField("3", 3);
        sReminderField.setHorizontalAlignment(SwingConstants.CENTER);
        sReminderField.setFont(new Font("Serif", Font.PLAIN, 30));
        sReminderField.setOpaque(true);
        sReminderField.setBackground(Color.black);
        sReminderField.setForeground(Color.white);

        sReminderBox = new JCheckBox(" Reminder [min] ", true);
        sReminderBox.setHorizontalTextPosition(SwingConstants.LEFT);
        sReminderBox.setFont(new Font("Serif", Font.PLAIN, 25));
        sReminderBox.setOpaque(true);
        sReminderBox.setBackground(Color.black);
        sReminderBox.setForeground(Color.white);

        sPresetLabel = new JLabel("  Presets: ");
        sPresetLabel.setFont(new Font("Serif", Font.PLAIN, 25));
        sPresetLabel.setBackground(Color.black);
        sPresetLabel.setForeground(Color.white);

        sPresetBox = new JComboBox(new String[]{
            "Custom", "25-5-5", "16-4-4", "12-3-3", "8-2-2"
        });
        sPresetBox.setFont(new Font("Serif", Font.PLAIN, 25));
        sPresetBox.setOpaque(true);
        sPresetBox.setBackground(Color.black);
        sPresetBox.setForeground(Color.white);

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
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }

        Counter sCounter = new Counter();
        sCounter.setExtendedState(JFrame.MAXIMIZED_BOTH);
        sCounter.setVisible(true);
     }
}
