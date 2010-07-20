package com.jds.jn.gui.forms;

import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;

import com.intellij.uiDesigner.core.Spacer;
import com.jds.jn.config.RValues;
import com.jds.jn.gui.JActionEvent;
import com.jds.jn.gui.JActionListener;
import com.jds.jn.gui.listeners.HideWatcher;
import com.jds.jn.gui.listeners.WindowsAdapter;
import com.jds.jn.gui.panels.ViewPane;
import com.jds.jn.gui.panels.ViewTabbedPane;
import com.jds.jn.network.profiles.NetworkProfile;
import com.jds.jn.network.profiles.NetworkProfiles;
import com.jds.jn.session.Session;
import com.jds.jn.statics.RibbonActions;
import com.jds.jn.statics.TabRibbonActions;
import com.jds.jn.util.Bundle;
import com.jds.jn.util.ThreadPoolManager;
import com.jds.jn.version_control.Version;
import com.jds.swing.JTrayIcon;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.awt.AWTUtilities;
import org.jvnet.flamingo.ribbon.*;

/**
 * Author: VISTALL
 * Company: J Develop Station
 * Date: 03/01/2010
 * Time: 22:31:09
 */
public class MainForm extends JRibbonFrame
{
	private static MainForm _instance;
	private static final Logger _log = Logger.getLogger(MainForm.class);

	public static void init() throws Exception
	{
		_instance = new MainForm();
	}

	public static MainForm getInstance()
	{
		return _instance;
	}

	private JPanel _panel1;

	private JProgressBar _memoryBar;
	private JButton _gcButton;
	private JButton _consoleBtn;
	private JProgressBar _readerProgress;
	private ViewTabbedPane _sessionTabbedPane;

	private JTrayIcon _trayIcon;

	private ScheduledFuture<?> _memoryBarTask;

	private RibbonContextualTaskGroup _sessionGroup;

	public MainForm() throws Exception
	{
		super("Jn");

		$$$setupUI$$$();
		add($$$getRootComponent$$$());
		initTray();
		setResizable(false);

		setDefaultCloseOperation(EXIT_ON_CLOSE); //setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setMinimumSize(new Dimension(500, 800));
		setExtendedState(JFrame.MAXIMIZED_HORIZ);

		try
		{
			setIconImages(Arrays.asList(ImageIO.read(getClass().getResource("/com/jds/jn/resources/nimg/Jn24.png")), ImageIO.read(getClass().getResource("/com/jds/jn/resources/nimg/Jn.png"))));
		}
		catch (IOException e)
		{
			_log.info("Exception: " + e, e);
		}

		RibbonApplicationMenu menu = new RibbonApplicationMenu();
		RibbonActions.ribbonMenu(menu);
		getRibbon().setApplicationMenu(menu);

		RibbonTask f = new RibbonTask(Bundle.getString("Main"), new AbstractRibbonBand[]{
				RibbonActions.files(),
				RibbonActions.listeners()
		});
		getRibbon().addTask(f);

		RibbonTask s = new RibbonTask(Bundle.getString("Settings"), new AbstractRibbonBand[]{RibbonActions.settings()});
		getRibbon().addTask(s);

		TabRibbonActions tabs = new TabRibbonActions();
		_sessionGroup = tabs.getGroup();

		getRibbon().addContextualTaskGroup(_sessionGroup);

		addWindowListener(new WindowsAdapter());

		_gcButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JActionListener.handle(JActionEvent.GC, _gcButton);
			}
		});

		_consoleBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JActionListener.handle(JActionEvent.CONSOLE_WINDOW, e.getSource());
			}
		});

	}

	private void initTray()
	{
		if (!SystemTray.isSupported())
		{
			return;
		}

		try
		{
			SystemTray st = SystemTray.getSystemTray();
			_trayIcon = new JTrayIcon(ImageIO.read(getClass().getResource("/com/jds/jn/resources/nimg/Jn24.png")));
			_trayIcon.setImageAutoSize(true);
			_trayIcon.setToolTip(Version.CURRENT.toString());


			JPopupMenu pm = new JPopupMenu();

			final JMenuItem restore = new JMenuItem("Hide");
			restore.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					JActionListener.handle(JActionEvent.HIDE_SHOW, restore);
				}
			});
			pm.add(restore);

			pm.addSeparator();

			JMenuItem itemAbout = new JMenuItem("About");
			//itemAbout.setActionCommand(MainForm.MainAction.ABOUT.name());
			//itemAbout.addActionListener(_listener);
			pm.add(itemAbout);


			final JMenuItem exit = new JMenuItem("Exit");
			exit.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					JActionListener.handle(JActionEvent.EXIT, exit);
				}
			});
			pm.add(exit);

			_trayIcon.setJPopupMenu(pm);

			_trayIcon.addMouseListener(new HideWatcher(restore));

			st.add(_trayIcon);
		}
		catch (Exception e)
		{
			_log.info("Exception: " + e, e);
		}
	}

	public void startMemoryBarTask()
	{
		_memoryBarTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				updateMemoryBar();
			}
		}, 5000, 5000);
	}

	public void stopMemoryBarTask()
	{
		if (_memoryBarTask != null)
		{
			_memoryBarTask.cancel(true);
			_memoryBarTask = null;
		}
	}

	public void updateMemoryBar()
	{
		MemoryUsage hm = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

		long use = hm.getUsed() / 1048576;
		long max = hm.getMax() / 1048576;
		byte persents = (byte) ((use * 100) / max);

		_memoryBar.setString(use + " MB of " + max + " MB");
		_memoryBar.setValue(persents);
		_memoryBar.setToolTipText("Total heap size: " + max + " MB Used: " + use + "MB");
	}

	public void showSession(Session s)
	{
		s.show();
		sessionMenu(true);
		getViewTabbedPane().showSession(s);
	}

	public void sessionMenu(boolean b)
	{
		getRibbon().setVisible(_sessionGroup, b);
	}

	public void closeSessionTab(ViewPane vp)
	{
		if (vp == null || vp.getSession() == null)
		{
			return;
		}

		vp.getSession().close();
		getViewTabbedPane().remove(vp);

		if (getViewTabbedPane().sizeAll() == 0)
		{
			sessionMenu(false);
		}
	}

	public void info(String text)
	{
		_log.info(text);
	}

	public void warn(String text)
	{
		warn(text, null);
	}

	public void warn(String text, Throwable e)
	{
		_log.info(text, e);
	}

	public ViewTabbedPane getViewTabbedPane()
	{
		return _sessionTabbedPane;
	}

	public void updateTitle()
	{
		NetworkProfile prof = NetworkProfiles.getInstance().active();

		if (prof != null)
		{
			setTitle(String.format("%s - [%s]", Version.CURRENT, prof.getName()));
		}
		else
		{
			setTitle(Version.CURRENT.toString());
		}
	}

	public JProgressBar getProgressBar()
	{
		return _readerProgress;
	}

	public void updateVisible()
	{
		AWTUtilities.setWindowOpacity(this, RValues.MAIN_VISIBLE.asFloat());
	}


	private void createUIComponents()
	{
		_sessionTabbedPane = new ViewTabbedPane();
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
		_panel1 = new JPanel();
		_panel1.setLayout(new FormLayout("fill:d:grow", "center:d:grow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
		final JToolBar toolBar1 = new JToolBar();
		toolBar1.setEnabled(true);
		toolBar1.setFloatable(false);
		toolBar1.setMargin(new Insets(2, 2, 2, 2));
		CellConstraints cc = new CellConstraints();
		_panel1.add(toolBar1, cc.xy(1, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
		_consoleBtn = new JButton();
		_consoleBtn.setBorderPainted(false);
		_consoleBtn.setFocusPainted(false);
		_consoleBtn.setHorizontalTextPosition(0);
		_consoleBtn.setIcon(new ImageIcon(getClass().getResource("/com/jds/jn/resources/images/file.png")));
		_consoleBtn.setText("");
		_consoleBtn.setVisible(true);
		toolBar1.add(_consoleBtn);
		final Spacer spacer1 = new Spacer();
		toolBar1.add(spacer1);
		_readerProgress = new JProgressBar();
		_readerProgress.setEnabled(true);
		_readerProgress.setMaximumSize(new Dimension(200, 20));
		_readerProgress.setMinimumSize(new Dimension(200, 20));
		_readerProgress.setPreferredSize(new Dimension(200, 20));
		_readerProgress.setStringPainted(true);
		_readerProgress.setVerifyInputWhenFocusTarget(true);
		_readerProgress.setVisible(false);
		toolBar1.add(_readerProgress);
		final Spacer spacer2 = new Spacer();
		toolBar1.add(spacer2);
		_memoryBar = new JProgressBar();
		_memoryBar.setMaximumSize(new Dimension(200, 20));
		_memoryBar.setMinimumSize(new Dimension(200, 20));
		_memoryBar.setPreferredSize(new Dimension(200, 20));
		_memoryBar.setStringPainted(true);
		toolBar1.add(_memoryBar);
		_gcButton = new JButton();
		_gcButton.setBorderPainted(false);
		_gcButton.setContentAreaFilled(true);
		_gcButton.setDefaultCapable(false);
		_gcButton.setFocusPainted(false);
		_gcButton.setIcon(new ImageIcon(getClass().getResource("/com/jds/jn/resources/nimg/gc.png")));
		_gcButton.setRequestFocusEnabled(false);
		_gcButton.setRolloverEnabled(true);
		_gcButton.setText("");
		toolBar1.add(_gcButton);
		_sessionTabbedPane = new ViewTabbedPane();
		_panel1.add(_sessionTabbedPane.$$$getRootComponent$$$(), cc.xy(1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$()
	{
		return _panel1;
	}
}
