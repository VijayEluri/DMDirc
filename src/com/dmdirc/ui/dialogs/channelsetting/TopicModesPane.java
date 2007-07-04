/*
 * Copyright (c) 2006-2007 Chris Smith, Shane Mc Cormack, Gregory Holmes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.dmdirc.ui.dialogs.channelsetting;

import com.dmdirc.logger.Logger;
import com.dmdirc.Channel;
import com.dmdirc.logger.ErrorLevel;
import static com.dmdirc.ui.UIUtilities.SMALL_BORDER;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Topic panel.
 */
public class TopicModesPane extends JPanel implements KeyListener {

    /**
     * A version number for this class. It should be changed whenever the class
     * structure is changed (or anything else that would prevent serialized
     * objects being unserialized with the new class).
     */
    private static final long serialVersionUID = 1;

    /** Parent channel. */
    private Channel channel;

    /** Parent dialog. */
    private ChannelSettingsDialog parent;

    /**
     * the maximum length allowed for a topic.
     */
    private int topicLengthMax;

    /**
     * label showing the number of characters left in a topic.
     */
    private JLabel topicLengthLabel;

    /**
     * Topic text entry text area.
     */
    private JTextArea topicText;

    /**
     * Creates a new instance of TopicModesPane.
     *
     * @param channel Parent channel
     * @param parent Parent dialog
     */
    public TopicModesPane(final Channel channel,
            final ChannelSettingsDialog parent) {

        this.channel = channel;
        this.parent = parent;

        final Map<String, String> iSupport =
                channel.getServer().getParser().get005();
        if (iSupport.containsKey("TOPICLEN")) {
            try {
                topicLengthMax =
                        Integer.parseInt(iSupport.get("TOPICLEN"));
            } catch (NumberFormatException ex) {
                topicLengthMax = 250;
                Logger.userError(ErrorLevel.LOW,
                        "IRCD doesnt supply topic length");
            }
        }

        initTopicsPanel();

        setVisible(true);
    }
    
    /** Updates the panel. */
    public void update() {
        setVisible(false);
        
        removeAll();
        initTopicsPanel();
        
        setVisible(true);
    }

    /**
     * Initialises the topic panel.
     */
    private void initTopicsPanel() {
        final GridBagConstraints constraints = new GridBagConstraints();
        final JLabel topicWho = new JLabel();
        final String topic = channel.getChannelInfo().getTopic();
        final JScrollPane scrollPane;
        topicLengthLabel = new JLabel();
        topicText = new JTextArea(100, 4);

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Channel Topic"),
                BorderFactory.createEmptyBorder(SMALL_BORDER, SMALL_BORDER,
                SMALL_BORDER, SMALL_BORDER)));

        topicText.setText(channel.getChannelInfo().getTopic());
        topicText.setLineWrap(true);
        topicText.addKeyListener(this);
        topicText.setWrapStyleWord(true);
        topicText.setRows(5);
        topicText.setColumns(30);
        scrollPane = new JScrollPane(topicText);
        
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.weightx = 1.0;
        constraints.gridy = 1;
        add(scrollPane, constraints);

        if (topicLengthMax == 0) {
            topicLengthLabel.setText(topicText.getText().length() + " characters");
        } else {
            topicLengthLabel.setText(topicLengthMax -
                    topicText.getText().length() + " of " + topicLengthMax +
                    " available");
        }
        
        constraints.gridy =2;
        add(topicLengthLabel, constraints);
        
        topicWho.setSize(30, 0);
        topicWho.setBorder(BorderFactory.createEmptyBorder(SMALL_BORDER, 0, 0, 0));
        if ("".equals(topic)) {
            topicWho.setText("No topic set.");
        } else {
            topicWho.setText("<html>Set by " +
                    channel.getChannelInfo().getTopicUser() + "<br> on " +
                    new Date(1000 * channel.getChannelInfo().getTopicTime()) +
                    "</html>");
        }
        
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridy = 3;
        add(topicWho, constraints);
    }

    /** Processes the topic and changes it if necessary. */
    protected void setChangedTopic() {
        if (!channel.getChannelInfo().getTopic().equals(topicText.getText())) {
            channel.getServer().getParser().
                    sendLine("TOPIC " + channel.getChannelInfo().getName() + "" +
                    " :" + topicText.getText());
        }
    }

    /** {@inheritDoc}. */
    @Override
    public void keyTyped(final KeyEvent keyEvent) {
        if (topicText.getText().length() >= topicLengthMax && topicLengthMax != 0 &&
                (keyEvent.getKeyCode() != KeyEvent.VK_BACK_SPACE &&
                keyEvent.getKeyCode() != KeyEvent.VK_DELETE)) {
            keyEvent.consume();
        }
        if (topicLengthMax == 0) {
            topicLengthLabel.setText(topicText.getText().length() + " characters");
        } else {
            topicLengthLabel.setText((topicLengthMax -
                    topicText.getText().length()) + " of " + topicLengthMax +
                    " available");
        }
    }

    /** {@inheritDoc}. */
    @Override
    public void keyPressed(final KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER &&
                keyEvent.getSource() == topicText) {
            keyEvent.consume();
            parent.save();
        }
    }

    /** {@inheritDoc}. */
    @Override
    public void keyReleased(final KeyEvent keyEvent) {
        //ignore, unused.
    }
}
