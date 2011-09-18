package com.jds.jn.gui.dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.jds.jn.Jn;
import com.jds.jn.config.RValues;
import com.jds.jn.gui.JActionEvent;
import com.jds.jn.gui.JActionListener;
import com.jds.jn.gui.forms.MainForm;
import com.jds.jn.gui.panels.NetworkSettingPane;
import com.jds.jn.network.profiles.NetworkProfile;
import com.jds.jn.network.profiles.NetworkProfilePart;
import com.jds.jn.network.profiles.NetworkProfiles;

public class NetworkSettingsDialog extends JDialog
{
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTabbedPane _tabbedPane1;
	private JTabbedPane _setTabPane;
	private JComboBox _profiles;
	private JButton _addProfileButton;
	private JTextField _protocolDir;
	private JButton _openProtocol;
	private JButton _delProtocol;
	private JButton _saveButton;

	public NetworkSettingsDialog()
	{
		super(MainForm.getInstance(), "Network Settings", true);

		//setLocationByPlatform(false);

		setContentPane(contentPane);
		getRootPane().setDefaultButton(buttonOK);
		setSize(700, 500);

		buttonOK.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onOK();
			}
		});

		buttonCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onCancel();
			}
		});

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				onCancel();
			}
		});

		contentPane.registerKeyboardAction(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		_addProfileButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				JActionListener.handle(JActionEvent.ADD_PROFILE, _addProfileButton, NetworkSettingsDialog.this);
			}
		});

		_delProtocol.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				NetworkProfile prof = (NetworkProfile) _profiles.getSelectedItem();
				if(prof == null)
				{
					return;
				}

				NetworkProfiles.getInstance().removeProfile(prof);

				load();
			}
		});

		_profiles.addItemListener(new ItemListener()
		{

			@Override
			public void itemStateChanged(ItemEvent e)
			{
				NetworkProfile prof = (NetworkProfile) _profiles.getSelectedItem();
				if(prof == null)
				{
					return;
				}

				_setTabPane.removeAll();

				for(NetworkProfilePart part : prof.parts())
				{
					_setTabPane.addTab(part.getType().name().replace("_", ""), new NetworkSettingPane(part));
				}
			}
		});


		_saveButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				save();
			}
		});

		load();
	}

	public void load()
	{
		_setTabPane.removeAll();
		_profiles.removeAllItems();

		for(NetworkProfile profile : NetworkProfiles.getInstance().profiles())
		{
			_profiles.addItem(profile);
		}

		NetworkProfile prof = NetworkProfiles.getInstance().getProfile(RValues.ACTIVE_PROFILE.asString());
		if(prof != null)
		{
			_profiles.setSelectedItem(prof);
		}
	}

	public void save()
	{
		NetworkProfile prof = (NetworkProfile) _profiles.getSelectedItem();
		if(prof == null)
		{
			return;
		}

		for(Component component : _setTabPane.getComponents())
		{
			if(component instanceof NetworkSettingPane)
			{
				NetworkSettingPane pane = (NetworkSettingPane) component;
				pane.set(pane.type(), prof);
			}
		}

		RValues.ACTIVE_PROFILE.setVal(prof.getName());
	}

	private void onOK()
	{
		save();

		Jn.getForm().updateTitle();

		dispose();
	}

	private void onCancel()
	{
		dispose();
	}

	private void createUIComponents()
	{

	}

	{
		// GUI initializer generated by IntelliJ IDEA GUI Designer
		// >>> IMPORTANT!! <<<
		// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$()
	{
		contentPane = new JPanel();
		contentPane.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		contentPane.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
		final Spacer spacer1 = new Spacer();
		panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
		panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		buttonOK = new JButton();
		buttonOK.setText("OK");
		panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		buttonCancel = new JButton();
		buttonCancel.setText("Cancel");
		panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel3 = new JPanel();
		panel3.setLayout(new GridLayoutManager(1, 1, new Insets(1, 1, 1, 1), -1, -1));
		contentPane.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		_tabbedPane1 = new JTabbedPane();
		_tabbedPane1.setTabLayoutPolicy(0);
		_tabbedPane1.setTabPlacement(3);
		panel3.add(_tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
		final JPanel panel4 = new JPanel();
		panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		_tabbedPane1.addTab("Network", panel4);
		final JPanel panel5 = new JPanel();
		panel5.setLayout(new GridLayoutManager(1, 1, new Insets(4, 4, 4, 4), -1, -1));
		panel4.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		_setTabPane = new JTabbedPane();
		panel5.add(_setTabPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
		final JPanel panel6 = new JPanel();
		panel6.setLayout(new GridLayoutManager(2, 1, new Insets(5, 5, 5, 5), -1, -1));
		_tabbedPane1.addTab("Protocol", panel6);
		final JPanel panel7 = new JPanel();
		panel7.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
		panel6.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JLabel label1 = new JLabel();
		label1.setText("Protocol Dir:");
		panel7.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		_protocolDir = new JTextField();
		panel7.add(_protocolDir, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		_openProtocol = new JButton();
		_openProtocol.setIcon(new ImageIcon(getClass().getResource("/com/jds/jn/resources/images/folderOpen.png")));
		_openProtocol.setText("");
		panel7.add(_openProtocol, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer2 = new Spacer();
		panel6.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JPanel panel8 = new JPanel();
		panel8.setLayout(new GridLayoutManager(1, 1, new Insets(1, 1, 1, 1), -1, -1));
		contentPane.add(panel8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
		final JToolBar toolBar1 = new JToolBar();
		toolBar1.setFloatable(false);
		panel8.add(toolBar1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setText("Profile:");
		toolBar1.add(label2);
		_profiles = new JComboBox();
		toolBar1.add(_profiles);
		_saveButton = new JButton();
		_saveButton.setIcon(new ImageIcon(getClass().getResource("/com/jds/jn/resources/images/save.png")));
		_saveButton.setText("");
		toolBar1.add(_saveButton);
		_addProfileButton = new JButton();
		_addProfileButton.setBorderPainted(false);
		_addProfileButton.setContentAreaFilled(true);
		_addProfileButton.setDefaultCapable(false);
		_addProfileButton.setFocusPainted(false);
		_addProfileButton.setFocusable(true);
		_addProfileButton.setHorizontalTextPosition(0);
		_addProfileButton.setIcon(new ImageIcon(getClass().getResource("/com/jds/jn/resources/images/add.png")));
		_addProfileButton.setRequestFocusEnabled(true);
		_addProfileButton.setRolloverEnabled(true);
		_addProfileButton.setText("");
		_addProfileButton.setVerifyInputWhenFocusTarget(true);
		_addProfileButton.setVisible(true);
		toolBar1.add(_addProfileButton);
		_delProtocol = new JButton();
		_delProtocol.setBorderPainted(false);
		_delProtocol.setFocusPainted(false);
		_delProtocol.setIcon(new ImageIcon(getClass().getResource("/com/jds/jn/resources/images/dell.png")));
		_delProtocol.setText("");
		toolBar1.add(_delProtocol);
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$()
	{
		return contentPane;
	}
}
