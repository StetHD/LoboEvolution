/*
    GNU GENERAL LICENSE
    Copyright (C) 2014 - 2018 Lobo Evolution

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    verion 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General License for more details.

    You should have received a copy of the GNU General Public
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    

    Contact info: ivan.difrancesco@yahoo.it
 */
package org.loboevolution.primary.ext;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.loboevolution.primary.action.AboutAction;
import org.loboevolution.primary.action.AddBookmarkAction;
import org.loboevolution.primary.action.BackAction;
import org.loboevolution.primary.action.BackMoreAction;
import org.loboevolution.primary.action.BlankWindowAction;
import org.loboevolution.primary.action.ClearCacheAction;
import org.loboevolution.primary.action.ClonedWindowAction;
import org.loboevolution.primary.action.CopyAction;
import org.loboevolution.primary.action.ExitAction;
import org.loboevolution.primary.action.ForwardAction;
import org.loboevolution.primary.action.ForwardMoreAction;
import org.loboevolution.primary.action.FullScreenAction;
import org.loboevolution.primary.action.GoAction;
import org.loboevolution.primary.action.OpenFileAction;
import org.loboevolution.primary.action.PreferencesAction;
import org.loboevolution.primary.action.ReloadAction;
import org.loboevolution.primary.action.SaveFileAction;
import org.loboevolution.primary.action.ScreenShotAction;
import org.loboevolution.primary.action.SearchAction;
import org.loboevolution.primary.action.ShowBookmarksAction;
import org.loboevolution.primary.action.ShowRecentHostsAction;
import org.loboevolution.primary.action.SourceAction;
import org.loboevolution.primary.action.StopAction;
import org.loboevolution.primary.gui.bookmarks.BookmarksHistory;
import org.loboevolution.primary.info.BookmarkInfo;
import org.loboevolution.primary.settings.SearchEngine;
import org.loboevolution.primary.settings.ToolsSettings;
import org.loboevolution.request.ClientletRequestHandler;
import org.loboevolution.ua.NavigationEntry;
import org.loboevolution.ua.NavigatorProgressEvent;
import org.loboevolution.ua.NavigatorWindow;
import org.loboevolution.ua.NavigatorWindowEvent;
import org.loboevolution.ua.NavigatorWindowListener;
import org.loboevolution.ua.RequestType;
import org.loboevolution.util.Timing;

/**
 * The Class ComponentSource.
 */
public class ComponentSource implements NavigatorWindowListener {

	/** The Constant logger. */
	private static final Logger logger = LogManager.getLogger(ComponentSource.class);

	/** The Constant PREFERRED_MAX_MENU_SIZE. */
	private static final int PREFERRED_MAX_MENU_SIZE = 20;

	/** The window. */
	private final NavigatorWindow window;

	/** The address field. */
	private final AddressField addressField;

	/** The status message component. */
	private final JLabel statusMessageComponent;

	/** The progress bar. */
	private final ProgressBar progressBar;

	/** The recent bookmarks menu. */
	private final JMenu recentBookmarksMenu;

	/** The tagged bookmarks menu. */
	private final JMenu taggedBookmarksMenu;

	/** The back more menu. */
	private final JMenu backMoreMenu;

	/** The forward more menu. */
	private final JMenu forwardMoreMenu;

	/** The searchers menu. */
	private final JMenu searchersMenu;

	/** The search button. */
	private final JButton searchButton;

	/** The action pool. */
	private final ActionPool actionPool;
	
	/** The status message. */
	private String statusMessage;
	
	/** The default status message. */
	private String defaultStatusMessage;

	/**
	 * Instantiates a new component source.
	 *
	 * @param window
	 *            the window
	 */
	public ComponentSource(final NavigatorWindow window) {
		super();
		this.actionPool = new ActionPool(this, window);
		this.window = window;
		this.addressField = new AddressField(this);
		this.progressBar = new ProgressBar();
		this.statusMessageComponent = new JLabel();
		this.searchButton = this.getSearchButton();
		this.updateSearchButtonTooltip();
		JMenu bookmarksMenu = new JMenu("Recent Bookmarks");
		this.recentBookmarksMenu = bookmarksMenu;
		bookmarksMenu.setMnemonic('R');
		bookmarksMenu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuSelected(MenuEvent e) {
				populateRecentBookmarks();
			}
		});
		JMenu taggedBookmarksMenu = new JMenu("Tagged Bookmarks");
		this.taggedBookmarksMenu = taggedBookmarksMenu;
		taggedBookmarksMenu.setMnemonic('T');
		taggedBookmarksMenu.setToolTipText("Shows up to " + PREFERRED_MAX_MENU_SIZE + " tags with up to "
				+ PREFERRED_MAX_MENU_SIZE + " recent bookmarks each.");
		taggedBookmarksMenu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuSelected(MenuEvent e) {
				populateTaggedBookmarks();
			}
		});
		JMenu backMoreMenu = new JMenu();
		// BackMoreAction only used for enabling
		backMoreMenu.setAction(new BackMoreAction(window, actionPool));
		backMoreMenu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuSelected(MenuEvent e) {
				populateBackMore();
			}
		});
		this.backMoreMenu = backMoreMenu;
		backMoreMenu.setText("Back To");
		JMenu forwardMoreMenu = new JMenu();
		// ForwardMoreAction only used for enabling
		forwardMoreMenu.setAction(new ForwardMoreAction(window, actionPool));
		forwardMoreMenu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuSelected(MenuEvent e) {
				populateForwardMore();
			}
		});
		this.forwardMoreMenu = forwardMoreMenu;
		forwardMoreMenu.setText("Forward To");

		JMenu searchersMenu = new JMenu();
		searchersMenu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuSelected(MenuEvent e) {
				populateSearchers();
			}
		});
		this.searchersMenu = searchersMenu;
		searchersMenu.setText("Searching With");
		searchersMenu.setToolTipText("Select the search engine that is used by the Search button in the address bar.");
	}

	/**
	 * Gets the address bar components.
	 *
	 * @return the address bar components
	 */
	public Component[] getAddressBarComponents() {
		return new Component[] { this.getBackButton(), this.window.createGap(), this.getForwardButton(),
				this.window.createGap(), this.getStopButton(), this.window.createGap(), this.getRefreshButton(),
				this.window.createGap(), this.window.createGlueComponent(this.addressField, true),
				this.window.createGap(), this.getGoButton(), this.window.createGap(), this.searchButton,
				this.window.createGap() };
	}

	/**
	 * Gets the status bar components.
	 *
	 * @return the status bar components
	 */
	public Component[] getStatusBarComponents() {
		return new Component[] { this.window.createGap(), this.getStatusMessageComponent(), this.window.createGap(),
				this.getProgressBar(), this.window.createGap() };
	}

	/**
	 * Gets the file menu.
	 *
	 * @return the file menu
	 */
	public JMenu getFileMenu() {
		JMenu menu = new JMenu("File");
		menu.setMnemonic('F');
		menu.add(menuItem("Save As", 'S', "ctrl S", new SaveFileAction(this, window)));
		menu.addSeparator();
		menu.add(menuItem("Blank Window", 'B',
				KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK),
				new BlankWindowAction(this, window)));
		menu.addSeparator();
		menu.add(menuItem("Cloned Window", 'C', new ClonedWindowAction(window, actionPool)));
		menu.addSeparator();
		menu.add(menuItem("File...", 'F', "ctrl O", new OpenFileAction(this, window)));
		menu.addSeparator();
		menu.add(menuItem("Close", 'C', new ExitAction(this, window)));

		return menu;
	}

	/**
	 * Gets the edits the menu.
	 *
	 * @return the edits the menu
	 */
	public JMenu getEditMenu() {
		JMenu menu = new JMenu("Edit");
		menu.setMnemonic('E');
		menu.add(menuItem("Copy", 'C', "ctrl C", new CopyAction(window, actionPool)));
		return menu;
	}

	/**
	 * Gets the view menu.
	 *
	 * @return the view menu
	 */
	public JMenu getViewMenu() {
		JMenu menu = new JMenu("View");
		menu.setMnemonic('V');
		menu.add(menuItem("Page Source", 'S', new SourceAction(this, window, actionPool)));
		menu.add(menuItem("Full Screen", ' ', KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0),
				new FullScreenAction(window, actionPool)));
		return menu;
	}

	/**
	 * Gets the bookmarks menu.
	 *
	 * @return the bookmarks menu
	 */
	public JMenu getBookmarksMenu() {
		JMenu menu = new JMenu("Bookmarks");
		menu.setMnemonic('B');
		menu.add(menuItem("Add Bookmark", 'A', "ctrl shift a", new AddBookmarkAction(this, window)));
		menu.add(this.recentBookmarksMenu);
		menu.add(this.taggedBookmarksMenu);
		menu.add(menuItem("Show All Bookmarks", 'S', new ShowBookmarksAction(this, window)));
		return menu;
	}

	/**
	 * Gets the navigation menu.
	 *
	 * @return the navigation menu
	 */
	public JMenu getNavigationMenu() {
		JMenu menu = new JMenu("Navigation");
		menu.setMnemonic('N');

		menu.add(menuItem("Back", 'B', "ctrl B", new BackAction(window, actionPool)));
		menu.add(menuItem("Forward", 'F', new ForwardAction(window, actionPool)));
		menu.add(menuItem("Stop", 'S', KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), new StopAction(this, window)));
		menu.add(menuItem("Reload", 'R', KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), new ReloadAction(window, actionPool)));
		menu.addSeparator();
		menu.add(this.backMoreMenu);
		menu.add(this.forwardMoreMenu);

		return menu;
	}

	/**
	 * Gets the chronology menu.
	 *
	 * @return the chronology menu
	 */
	public JMenu getChronologyMenu() {
		JMenu menu = new JMenu("Recent Hosts");
		menu.removeAll();
		Collection<HostEntry> hostEntries = NavigationHistory.getInstance()
				.getRecentHostEntries(PREFERRED_MAX_MENU_SIZE);
		for (HostEntry entry : hostEntries) {
			String urlText = "http://" + entry.getHost();
			try {
				URL url = new URL(urlText);
				long elapsed = System.currentTimeMillis() - entry.getTimestamp();
				String menuText = entry.getHost() + " (" + Timing.getElapsedText(elapsed) + " ago)";
				Action action = this.actionPool.createNavigateAction(url);
				JMenuItem menuItem = menuItem(menuText, action);
				menuItem.setToolTipText(url.toExternalForm());
				menu.add(menuItem);
			} catch (MalformedURLException mfu) {
				logger.error("populateRecentHosts(): Bad URL=" + urlText, mfu);
			}
		}
		menu.addSeparator();
		menu.add(menuItem("Show All Recent Hosts", new ShowRecentHostsAction(this, window)));
		return menu;
	}

	/**
	 * Gets the tools menu.
	 *
	 * @return the tools menu
	 */
	public JMenu getToolsMenu() {
		JMenu menu = new JMenu("Tools");
		menu.setMnemonic('T');
		menu.add(this.searchersMenu);
		menu.add(menuItem("Preferences...", 'P', new PreferencesAction(this, window)));
		menu.add(menuItem("Screenshot", ' ', "", new ScreenShotAction(window)));
		menu.add(menuItem("Clear Cache", 'M', "ctrl M", new ClearCacheAction(this, window)));
		return menu;
	}

	/**
	 * Gets the help menu.
	 *
	 * @return the help menu
	 */
	public JMenu getHelpMenu() {
		JMenu menu = new JMenu("Help");
		menu.setMnemonic('H');

		menu.add(menuItem("About Lobo", 'A', new AboutAction(this, window)));
		menu.addSeparator();
		menu.add(menuItem("Project Home Page",
				this.actionPool.createNavigateAction("http://sourceforge.net/projects/loboevolution/")));
		menu.add(menuItem("Development Home Page",
				this.actionPool.createNavigateAction("https://github.com/oswetto/Loboevolution/")));
		menu.add(menuItem("Bug tracking",
				this.actionPool.createNavigateAction("https://github.com/oswetto/Loboevolution/issues/")));
		menu.addSeparator();
		menu.add(menuItem("Wiki",
				this.actionPool.createNavigateAction("http://sourceforge.net/p/loboevolution/wiki/Home/")));
		menu.add(menuItem("Discussion Forum",
				this.actionPool.createNavigateAction("http://sourceforge.net/p/loboevolution/discussion/")));

		return menu;
	}

	/**
	 * Gets the back button.
	 *
	 * @return the back button
	 */
	private Component getBackButton() {
		JButton button = new JButton();
		button.setAction(new BackAction(window, actionPool));
		button.setIcon(IconFactory.getInstance().getIcon("/org/loboevolution/images/back.png"));
		button.setToolTipText("Back");
		return button;
	}

	/**
	 * Gets the forward button.
	 *
	 * @return the forward button
	 */
	private Component getForwardButton() {
		JButton button = new JButton();
		button.setAction(new ForwardAction(window, actionPool));
		button.setIcon(IconFactory.getInstance().getIcon("/org/loboevolution/images/forward.png"));
		button.setToolTipText("Forward");
		return button;
	}

	/**
	 * Gets the stop button.
	 *
	 * @return the stop button
	 */
	private Component getStopButton() {
		JButton button = new JButton();
		button.setAction(new StopAction(this, window));
		button.setIcon(IconFactory.getInstance().getIcon("/org/loboevolution/images/stop.png"));
		button.setToolTipText("Stop");
		return button;
	}

	/**
	 * Gets the refresh button.
	 *
	 * @return the refresh button
	 */
	private Component getRefreshButton() {
		JButton button = new JButton();
		button.setAction(new ReloadAction(window, actionPool));
		button.setIcon(IconFactory.getInstance().getIcon("/org/loboevolution/images/reload.png"));
		button.setToolTipText("Refresh");
		return button;
	}

	/**
	 * Gets the go button.
	 *
	 * @return the go button
	 */
	private Component getGoButton() {
		JButton button = new JButton();
		button.setAction(new GoAction(this, window));
		button.setIcon(IconFactory.getInstance().getIcon("/org/loboevolution/images/go.png"));
		button.setToolTipText("Navigate to URL");
		return button;
	}

	/**
	 * Gets the search button.
	 *
	 * @return the search button
	 */
	private JButton getSearchButton() {
		JButton button = new JButton();
		button.setAction(new SearchAction(this, window));
		button.setIcon(IconFactory.getInstance().getIcon("/org/loboevolution/images/search.png"));
		return button;
	}

	/**
	 * Update search button tooltip.
	 */
	private void updateSearchButtonTooltip() {
		JButton button = this.searchButton;
		ToolsSettings settings = ToolsSettings.getInstance();
		SearchEngine currentEngine = settings.getSelectedSearchEngine();
		String name = currentEngine == null ? "[none]" : currentEngine.getName();
		button.setToolTipText("<html><body>Current search engine: " + name + ".</body></html>");
	}

	/**
	 * Gets the status message component.
	 *
	 * @return the status message component
	 */
	private Component getStatusMessageComponent() {
		return this.window.createGlueComponent(this.statusMessageComponent, true);
	}

	/**
	 * Gets the progress bar.
	 *
	 * @return the progress bar
	 */
	private Component getProgressBar() {
		return this.progressBar;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.ua.NavigatorWindowListener#defaultStatusUpdated(org.
	 * loboevolution .ua.NavigatorWindowEvent)
	 */
	@Override
	public void defaultStatusUpdated(NavigatorWindowEvent event) {
		String defaultStatus = event.getMessage();
		this.defaultStatusMessage = event.getMessage();
		if (this.statusMessage == null) {
			this.statusMessageComponent.setText(defaultStatus);
		}
	}

	/**
	 * Whether the request should be saved as a recent history entry.
	 *
	 * @param requestType
	 *            the request type
	 * @return true, if is history request
	 */
	private boolean isHistoryRequest(RequestType requestType) {
		return requestType == RequestType.ADDRESS_BAR || requestType == RequestType.CLICK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.ua.NavigatorWindowListener#documentAccessed(org.
	 * loboevolution .ua.NavigatorWindowEvent)
	 */
	@Override
	public void documentAccessed(NavigatorWindowEvent event) {
		URL url = event.getUrl();
		if ("GET".equals(event.getMethod()) && isHistoryRequest(event.getRequestType())) {
			NavigationHistory.getInstance().addAsRecent(url, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.ua.NavigatorWindowListener#documentRendering(org.
	 * loboevolution .ua.NavigatorWindowEvent)
	 */
	@Override
	public void documentRendering(NavigatorWindowEvent event) {
		if (logger.isInfoEnabled()) {
			logger.info("documentRendering(): event=" + event);
		}
		if (this.window.getTopFrame() == event.getNavigatorFrame()) {
			URL url = event.getUrl();
			this.addressField.setUrl(url);
			this.clearState();
			this.actionPool.updateEnabling();
		}
	}

	/**
	 * Sets the navigation entry.
	 *
	 * @param entry
	 *            the new navigation entry
	 */
	public void setNavigationEntry(NavigationEntry entry) {
		if (entry != null) {
			if (this.window.getTopFrame() == entry.getNavigatorFrame()) {
				URL url = entry.getUrl();
				this.addressField.setUrl(url);
				this.clearState();
				this.actionPool.updateEnabling();
			}
		} else {
			this.clearState();
			this.addressField.setUrl(null);
			this.actionPool.updateEnabling();
		}
	}

	/**
	 * Clear state.
	 */
	private void clearState() {
		this.statusMessage = null;
		this.defaultStatusMessage = null;
		this.statusMessageComponent.setText("");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.ua.NavigatorWindowListener#progressUpdated(org.
	 * loboevolution .ua.NavigatorProgressEvent)
	 */
	@Override
	public void progressUpdated(NavigatorProgressEvent event) {
		if (this.window.getTopFrame() == event.getNavigatorFrame()) {
			this.progressBar.updateProgress(event.getProgressType(), event.getCurrentValue(), event.getMaxValue());
		}
		this.statusMessageComponent
				.setText(ClientletRequestHandler.getProgressMessage(event.getProgressType(), event.getUrl()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.loboevolution.ua.NavigatorWindowListener#statusUpdated(org.loboevolution.
	 * ua.NavigatorWindowEvent)
	 */
	@Override
	public void statusUpdated(NavigatorWindowEvent event) {
		String status = event.getMessage();
		this.statusMessage = status;
		this.statusMessageComponent.setText(status == null ? this.defaultStatusMessage : status);
	}

	/**
	 * Gets the potential matches.
	 *
	 * @param urlPrefix
	 *            the url prefix
	 * @param max
	 *            the max
	 * @return the potential matches
	 */
	public Collection<String> getPotentialMatches(String urlPrefix) {
		return NavigationHistory.getInstance().getHeadMatchItems(urlPrefix);
	}

	/**
	 * Gets the recent locations.
	 *
	 * @param max
	 *            the max
	 * @return the recent locations
	 */
	public Collection<String> getRecentLocations(int max) {
		return NavigationHistory.getInstance().getRecentItems(max);
	}

	/**
	 * Navigate.
	 *
	 * @param roughLocation
	 *            the rough location
	 * @param requestType
	 *            the request type
	 */
	public void navigate(String roughLocation, RequestType requestType) {
		try {
			this.window.stop();
			this.clearState();
			this.window.getTopFrame().navigate(roughLocation, requestType);
		} catch (MalformedURLException mfu) {
			ExtensionImpl.showError(this.window.getTopFrame(), null, new MalformedURLException(roughLocation));
		}
	}

	/**
	 * Navigate.
	 *
	 * @param url
	 *            the url
	 */
	public void navigate(URL url) {
		this.window.stop();
		this.clearState();
		this.window.getTopFrame().navigate(url);
	}

	/**
	 * Menu item.
	 *
	 * @param title
	 *            the title
	 * @param action
	 *            the action
	 * @return the j menu item
	 */
	public static JMenuItem menuItem(String title, Action action) {
		return menuItem(title, (char) 0, (KeyStroke) null, action);
	}

	/**
	 * Menu item.
	 *
	 * @param title
	 *            the title
	 * @param mnemonic
	 *            the mnemonic
	 * @param action
	 *            the action
	 * @return the j menu item
	 */
	public static JMenuItem menuItem(String title, char mnemonic, Action action) {
		return menuItem(title, mnemonic, (KeyStroke) null, action);
	}

	/**
	 * Menu item.
	 *
	 * @param title
	 *            the title
	 * @param mnemonic
	 *            the mnemonic
	 * @param accelerator
	 *            the accelerator
	 * @param action
	 *            the action
	 * @return the j menu item
	 */
	public static JMenuItem menuItem(String title, char mnemonic, KeyStroke accelerator, Action action) {
		JMenuItem item = new JMenuItem();
		item.setAction(action);
		item.setText(title);
		if (mnemonic != 0) {
			item.setMnemonic(mnemonic);
		}
		if (accelerator != null) {
			item.setAccelerator(accelerator);
		}
		return item;
	}

	/**
	 * Menu item.
	 *
	 * @param title
	 *            the title
	 * @param mnemonic
	 *            the mnemonic
	 * @param accelerator
	 *            the accelerator
	 * @param action
	 *            the action
	 * @return the j menu item
	 */
	public static JMenuItem menuItem(String title, char mnemonic, String accelerator, Action action) {
		KeyStroke keyStroke = accelerator == null ? null : KeyStroke.getKeyStroke(accelerator);
		return menuItem(title, mnemonic, keyStroke, action);
	}

	/**
	 * Populate recent bookmarks.
	 */
	public void populateRecentBookmarks() {
		JMenu bookmarksMenu = this.recentBookmarksMenu;
		bookmarksMenu.removeAll();
		Collection<HistoryEntry<BookmarkInfo>> historyEntries = BookmarksHistory.getInstance()
				.getRecentEntries(PREFERRED_MAX_MENU_SIZE);
		for (HistoryEntry<BookmarkInfo> hentry : historyEntries) {
			BookmarkInfo binfo = hentry.getItemInfo();
			String text = binfo.getTitle();
			URL url = binfo.getUrl();
			String urlText = url.toExternalForm();
			if (text == null || text.length() == 0) {
				text = urlText;
			}
			long elapsed = System.currentTimeMillis() - hentry.getTimetstamp();
			text = text + " (" + Timing.getElapsedText(elapsed) + " ago)";
			Action action = this.actionPool.createBookmarkNavigateAction(url);
			JMenuItem menuItem = ComponentSource.menuItem(text, action);
			StringBuffer toolTipText = new StringBuffer();
			toolTipText.append("<html>");
			toolTipText.append(urlText);
			String description = binfo.getDescription();
			if (description != null && description.length() != 0) {
				toolTipText.append("<br>");
				toolTipText.append(description);
			}
			menuItem.setToolTipText(toolTipText.toString());
			bookmarksMenu.add(menuItem);
		}
	}

	/**
	 * Populate tagged bookmarks.
	 */
	public void populateTaggedBookmarks() {
		JMenu bookmarksMenu = this.taggedBookmarksMenu;
		bookmarksMenu.removeAll();
		Collection<BookmarkInfo> bookmarkInfoList = BookmarksHistory.getInstance()
				.getRecentItemInfo(PREFERRED_MAX_MENU_SIZE * PREFERRED_MAX_MENU_SIZE);
		Map<String, JMenu> tagMenus = new HashMap<String, JMenu>();
		for (BookmarkInfo binfo : bookmarkInfoList) {
			URL url = binfo.getUrl();
			String urlText = url.toExternalForm();
			String[] tags = binfo.getTags();
			if (tags != null) {
				for (String tag : tags) {
					JMenu tagMenu = tagMenus.get(tag);
					if (tagMenu == null && tagMenus.size() < PREFERRED_MAX_MENU_SIZE) {
						tagMenu = new JMenu(tag);
						tagMenus.put(tag, tagMenu);
						bookmarksMenu.add(tagMenu);
					}
					if (tagMenu != null && tagMenu.getItemCount() < PREFERRED_MAX_MENU_SIZE) {
						String text = binfo.getTitle();
						if (text == null || text.length() == 0) {
							text = urlText;
						}
						Action action = this.actionPool.createBookmarkNavigateAction(url);
						JMenuItem menuItem = ComponentSource.menuItem(text, action);
						StringBuffer toolTipText = new StringBuffer();
						toolTipText.append("<html>");
						toolTipText.append(urlText);
						String description = binfo.getDescription();
						if (description != null && description.length() != 0) {
							toolTipText.append("<br>");
							toolTipText.append(description);
						}
						menuItem.setToolTipText(toolTipText.toString());
						tagMenu.add(menuItem);
					}
				}
			}
		}
	}

	/**
	 * Populate back more.
	 */
	public void populateBackMore() {
		NavigationEntry[] entries = this.window.getBackNavigationEntries();
		JMenu backMoreMenu = this.backMoreMenu;
		backMoreMenu.removeAll();
		for (NavigationEntry entry : entries) {
			String method = entry.getMethod();
			if ("GET".equals(method)) {
				String title = entry.getTitle();
				URL url = entry.getUrl();
				String text = title == null || title.length() == 0 ? url.toExternalForm() : title;
				Action action = this.actionPool.createGoToAction(entry);
				JMenuItem menuItem = menuItem(text, action);
				menuItem.setToolTipText(url.toExternalForm());
				backMoreMenu.add(menuItem);
			}
		}
		// backMoreMenu.revalidate();
	}

	/**
	 * Populate forward more.
	 */
	public void populateForwardMore() {
		NavigationEntry[] entries = this.window.getForwardNavigationEntries();
		JMenu forwardMoreMenu = this.forwardMoreMenu;
		forwardMoreMenu.removeAll();
		for (NavigationEntry entry : entries) {
			String method = entry.getMethod();
			if ("GET".equals(method)) {
				String title = entry.getTitle();
				URL url = entry.getUrl();
				String text = title == null || title.length() == 0 ? url.toExternalForm() : title;
				Action action = this.actionPool.createGoToAction(entry);
				JMenuItem menuItem = menuItem(text, action);
				menuItem.setToolTipText(url.toExternalForm());
				forwardMoreMenu.add(menuItem);
			}
		}
	}

	/**
	 * Checks for recent entries.
	 *
	 * @return true, if successful
	 */
	public boolean hasRecentEntries() {
		return NavigationHistory.getInstance().hasRecentEntries();
	}

	/**
	 * Show source.
	 */
	public void showSource() {
		String sourceCode = window.getTopFrame().getSourceCode();
		if (sourceCode == null) {
			sourceCode = "";
		}
		SourceViewerWindow textViewer = new SourceViewerWindow();
		textViewer.setText(sourceCode);
		textViewer.setSize(new Dimension(600, 400));
		textViewer.setLocationByPlatform(true);
		textViewer.setVisible(true);
	}

	/**
	 * Go.
	 */
	public void go() {
		this.navigateOrSearch();
	}

	/**
	 * Navigate or search.
	 */
	public void navigateOrSearch() {
		String addressText = this.addressField.getText();
		if (addressText.indexOf('.') == -1 && addressText.indexOf('/') == -1 && addressText.indexOf(':') == -1) {
			this.search();
		} else {
			this.navigate(addressText, RequestType.ADDRESS_BAR);
		}
	}

	/**
	 * Search.
	 */
	public void search() {
		ToolsSettings settings = ToolsSettings.getInstance();
		SearchEngine searchEngine = settings.getSelectedSearchEngine();
		if (searchEngine != null) {
			try {
				this.navigate(searchEngine.getURL(this.getAddressBarText()));
			} catch (MalformedURLException mfu) {
				window.getTopFrame().alert("Malformed search URL.");
			}
		}
	}

	/**
	 * Populate searchers.
	 */
	private void populateSearchers() {
		JMenu searchersMenu = this.searchersMenu;
		searchersMenu.removeAll();
		final ToolsSettings settings = ToolsSettings.getInstance();
		Collection<SearchEngine> searchEngines = settings.getSearchEngines();
		SearchEngine selectedEngine = settings.getSelectedSearchEngine();
		if (searchEngines != null) {
			for (SearchEngine se : searchEngines) {
				final SearchEngine finalSe = se;
				JRadioButtonMenuItem item = new JRadioButtonMenuItem();
				item.setAction(new AbstractAction() {

					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						settings.setSelectedSearchEngine(finalSe);
						settings.save();
						ComponentSource.this.updateSearchButtonTooltip();
					}
				});
				item.setSelected(se == selectedEngine);
				item.setText(se.getName());
				item.setToolTipText(se.getDescription());
				searchersMenu.add(item);
			}
		}
	}

	/**
	 * Gets the address bar text.
	 *
	 * @return the address bar text
	 */
	public String getAddressBarText() {
		return this.addressField.getText();
	}
}
