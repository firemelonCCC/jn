package com.jds.jn.gui.panels;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.jds.jn.gui.forms.MainForm;
import com.jds.jn.session.Session;
import com.jds.jn.util.Bundle;

/**
 * Author: VISTALL
 * Company: J Develop Station
 * Date: 04/01/2010
 * Time: 18:03:13
 */
public class ViewTabbedPane extends JTabbedPane
{
	private JPanel root;
	private JTabbedPane _sessionTabs;

	public ViewTabbedPane()
	{
		$$$setupUI$$$();
		addMouseListener(new MouseL());
	}

	public ViewPane getCurrentViewPane()
	{
		return (ViewPane) getSelectedComponent();
	}

	public void showSession(final Session s)
	{
		ViewPane viewPane = s.getViewPane();

		if (viewPane == null)
		{
			viewPane = new ViewPane(s);
		}

		addTab(String.format(Bundle.getString("Session") + ": %d", s.getSessionId()), viewPane);
	}

	public int sizeAll()
	{
		return getTabCount();
	}


	public class MouseL implements MouseListener
	{

		@Override
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
			{
				ViewPane pane = getCurrentViewPane();
				if (pane == null)
				{
					return;
				}
				if (pane.getSession() == null)
				{
					return;
				}

				// Session session = pane.getSession();
				/*ConfirmDialog dialog = new ConfirmDialog(MainForm.getInstance(), ResourceBundle.getBundle("com/jds/jn/resources/bundle/LanguageBundle").getString("Message"), ResourceBundle.getBundle("com/jds/jn/resources/bundle/LanguageBundle").getString("ConfirmCloseSessionMessage"));

									   if(Config.get(Values.CONFIRM_CLOSE_SESSION, true))
									{
										boolean[] result = dialog.showToConfirm();
										if(result[0])
										{
											Config.set(Values.CONFIRM_CLOSE_SESSION, result[1]);  */

				MainForm.getInstance().closeSessionTab(getCurrentViewPane());

				/*	}
									}
									else if(Config.get(Values.CONFIRM_CLOSE_NEW_SESSION, true) && session.getMethod() != null)
									{
										boolean[] result = dialog.showToConfirm();
										if(result[0])
										{
											Config.set(Values.CONFIRM_CLOSE_NEW_SESSION, result[1]);
											MainForm.getInstance().closeSessionTab(getCurrentViewPane());
										}
									}
									else
									{
										MainForm.getInstance().closeSessionTab(getCurrentViewPane());
									}*/
			}
		}

		@Override
		public void mousePressed(MouseEvent e)
		{

		}

		@Override
		public void mouseReleased(MouseEvent e)
		{

		}

		@Override
		public void mouseEntered(MouseEvent e)
		{

		}

		@Override
		public void mouseExited(MouseEvent e)
		{

		}
	}

	private void createUIComponents()
	{
		_sessionTabs = this;
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
		createUIComponents();
		root = new JPanel();
		root.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		root.add(_sessionTabs, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$()
	{
		return root;
	}
}
