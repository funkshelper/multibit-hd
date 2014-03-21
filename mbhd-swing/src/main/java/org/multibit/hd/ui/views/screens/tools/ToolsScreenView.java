package org.multibit.hd.ui.views.screens.tools;

import com.google.bitcoin.uri.BitcoinURI;
import com.google.bitcoin.uri.BitcoinURIParseException;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.RAGStatus;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.core.services.CoreServices;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.events.controller.ControllerEvents;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.models.AlertModel;
import org.multibit.hd.ui.models.Models;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.screens.AbstractScreenView;
import org.multibit.hd.ui.views.screens.Screen;
import org.multibit.hd.ui.views.wizards.Wizards;
import org.multibit.hd.ui.views.wizards.welcome.WelcomeWizardState;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * <p>View to provide the following to application:</p>
 * <ul>
 * <li>Provision of components and layout for the tools detail display</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class ToolsScreenView extends AbstractScreenView<ToolsScreenModel> {

  private JButton welcomeWizard;

  /**
   * @param panelModel The model backing this panel view
   * @param screen     The screen to filter events from components
   * @param title      The key to the main title of this panel view
   */
  public ToolsScreenView(ToolsScreenModel panelModel, Screen screen, MessageKey title) {
    super(panelModel, screen, title);
  }

  @Override
  public void newScreenModel() {

  }

  @Override
  public JPanel initialiseScreenViewPanel() {

    CoreServices.uiEventBus.register(this);

    MigLayout layout = new MigLayout(
      Panels.migXYLayout(),
      "[]10[]", // Column constraints
      "[]50[]" // Row constraints
    );

    JPanel contentPanel = Panels.newPanel(layout);

    Action showWelcomeWizardAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        Panels.showLightBox(Wizards.newClosingWelcomeWizard(WelcomeWizardState.WELCOME_SELECT_LANGUAGE).getWizardScreenHolder());
      }
    };

    final BitcoinURI bitcoinURI;
    try {
      bitcoinURI = new BitcoinURI("bitcoin:1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty?amount=0.01&label=Please%20donate%20to%20multibit.org");
    } catch (BitcoinURIParseException e) {
      ExceptionHandler.handleThrowable(e);
      return contentPanel;
    }

    Action acceptedBitcoinUriAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // Demonstrate a button
        AbstractAction action = new AbstractAction() {
          @Override
          public void actionPerformed(ActionEvent e) {

            ControllerEvents.fireRemoveAlertEvent();
            Panels.showLightBox(Wizards.newSendBitcoinWizard(bitcoinURI).getWizardScreenHolder());

          }
        };
        JButton button = Buttons.newAlertPanelButton(action, MessageKey.YES, AwesomeIcon.CHECK);

        // Create the alert
        AlertModel alertModel = Models.newAlertModel("Address '1AhN6rPdrMuKBGFDKR1k9A8SCLYaNgXhty' with label 'Please donate to multibit.org' is requesting '10mBTC'. Continue ?",
          RAGStatus.AMBER,
          button);

        ControllerEvents.fireAddAlertEvent(alertModel);
      }
    };

    welcomeWizard = Buttons.newShowWelcomeWizardButton(showWelcomeWizardAction);

    contentPanel.add(welcomeWizard, MultiBitUI.LARGE_BUTTON_MIG + ",align center,push");
    contentPanel.add(Buttons.newAddAlertButton(acceptedBitcoinUriAction), MultiBitUI.LARGE_BUTTON_MIG + ",align center, push,wrap");


    return contentPanel;
  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        welcomeWizard.requestFocusInWindow();
      }
    });

  }

}
