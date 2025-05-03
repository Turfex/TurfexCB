package de.pixeltyles.turfexcb;

import de.pixeltyles.turfexcb.Database.DataBaseManager;
import de.pixeltyles.turfexcb.JobSystem.JobManager;
import de.pixeltyles.turfexcb.commands.*;
import de.pixeltyles.turfexcb.listener.*;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TurfexCB extends JavaPlugin {

    private static TurfexCB instance;
    private DataBaseManager dbManager;
    private JobManager jobManager;

    public static TurfexCB getInstance() {
        return instance;
    }

    public DataBaseManager getDatabaseManager() {
        return dbManager;
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    @Override
    public void onEnable() {
        instance = this;

        dbManager = new DataBaseManager(
                "n55.tfc-hosting.de",
                "Turfex",
                "user_ab39fj2k",
                "P@ssw0rdQ7xZ",
                25572
        );
        dbManager.connect(
                "n55.tfc-hosting.de",
                "Turfex",
                "user_ab39fj2k",
                "P@ssw0rdQ7xZ",
                25572
        );


        LuckPerms luckPerms = LuckPermsProvider.get();


        jobManager = new JobManager(dbManager);

        getServer().getPluginManager().registerEvents(new ChatManager(dbManager, luckPerms), this);
        getServer().getPluginManager().registerEvents(new ScoreBoardListener(this), this);
        getServer().getPluginManager().registerEvents(new JobsListener(jobManager, dbManager), this);
        getServer().getPluginManager().registerEvents(new JobInventoryListener(jobManager), this);
        getServer().getPluginManager().registerEvents(new ConnectionListener(), this);



        getCommand("coins").setExecutor(new CoinsCommand(this, dbManager));
        getCommand("pay").setExecutor(new PayCommand(dbManager));
        getCommand("job").setExecutor(new JobCommand(jobManager));
        getCommand("supercoins").setExecutor(new SuperCoinsCommand(this, dbManager));
        getCommand("discord").setExecutor(new DiscordCommand());

        Bukkit.getConsoleSender().sendMessage("§a[Turfex] Plugin aktiviert.");
    }

    @Override
    public void onDisable() {
        if (dbManager != null) {
            dbManager.disconnect();
        }

        Bukkit.getConsoleSender().sendMessage("§c[Turfex] Plugin deaktiviert.");
    }
}
