package hytale.doryanbessiere.fr.villager.pages;

import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.BasicCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import hytale.doryanbessiere.fr.villager.dto.economy.bank.BankData;
import hytale.doryanbessiere.fr.villager.dto.economy.bank.account.BankAccountData;
import hytale.doryanbessiere.fr.villager.exceptions.bank.BankException;
import hytale.doryanbessiere.fr.villager.services.bank.BankService;
import java.util.List;
import javax.annotation.Nonnull;

public class BankAccountsPage extends BasicCustomUIPage {

    private final BankData bankData;

    public BankAccountsPage(@Nonnull PlayerRef playerRef, @Nonnull BankData bankData) {
        super(playerRef, CustomPageLifetime.CanDismiss);
        this.bankData = bankData;
    }

    @Override
    public void build(@Nonnull UICommandBuilder commandBuilder) {
        commandBuilder.append("Pages/BankAccountsPage.ui");
        commandBuilder.set("#BankName.Text", bankData.getName());

        List<BankAccountData> accounts;
        try {
            accounts = BankService.getAccountsById(bankData.getId());
        } catch (BankException e) {
            return;
        }
        int index = 0;
        for (BankAccountData account : accounts) {
            String selector = "#AccountList[" + index + "]";
            commandBuilder.append("#AccountList", "Pages/BankAccountRow.ui");
            commandBuilder.set(selector + " #Name.Text", account.getAccountName());
            commandBuilder.set(selector + " #Balance.Text", "Balance: " + account.getBalance());
            index++;
        }
    }
}
