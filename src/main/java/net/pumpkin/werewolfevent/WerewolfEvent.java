package net.pumpkin.werewolfevent;

import net.pumpkin.werewolfevent.commands.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class WerewolfEvent extends JavaPlugin {

    @Override
    public void onEnable() {

        // Create instances of the command executors
        RoleCommand rollCommand = new RoleCommand();
        SeerCommand seerCommand = new SeerCommand(rollCommand);
        CupidCommand cupidCommand = new CupidCommand(rollCommand);
        HunterCommand hunterCommand = new HunterCommand(rollCommand);
        VoteCommand voteCommand = new VoteCommand(rollCommand);
        RoleSee rollSee = new RoleSee(rollCommand);
        ReloadRoleUsage reloadRoleUsage = new ReloadRoleUsage(rollCommand, seerCommand, cupidCommand, hunterCommand);

        // Register commands
        Objects.requireNonNull(this.getCommand("night")).setExecutor(new WolfCommand(rollCommand));
        Objects.requireNonNull(this.getCommand("day")).setExecutor(new WolfCommand(rollCommand));
        Objects.requireNonNull(this.getCommand("role")).setExecutor(rollCommand);
        Objects.requireNonNull(this.getCommand("see")).setExecutor(seerCommand);
        Objects.requireNonNull(this.getCommand("checkrole")).setExecutor(new CheckRoleCommand(rollCommand));
        Objects.requireNonNull(this.getCommand("match")).setExecutor(cupidCommand);
        Objects.requireNonNull(this.getCommand("laststand")).setExecutor(hunterCommand);
        Objects.requireNonNull(this.getCommand("reloadroleusage")).setExecutor(reloadRoleUsage);
        Objects.requireNonNull(this.getCommand("eventinfo")).setExecutor(new EventCommand());
        Objects.requireNonNull(this.getCommand("vote")).setExecutor(voteCommand);
        Objects.requireNonNull(this.getCommand("startvote")).setExecutor(new StartVoteCommand(voteCommand));
        Objects.requireNonNull(this.getCommand("seerole")).setExecutor(rollSee);

        // Register events
        getServer().getPluginManager().registerEvents(cupidCommand, this);
        getServer().getPluginManager().registerEvents(hunterCommand, this);
        getServer().getPluginManager().registerEvents(new WolfCommand(rollCommand), this);  // Register the WolfCommand as a listener
        getServer().getPluginManager().registerEvents(new OnKillForceSpectator(this), this);  // Register OnKillForceSpectator as a listener
        getServer().getPluginManager().registerEvents(rollCommand, this);  // Register RoleCommand as a listener (assuming it's handling events)
        getServer().getPluginManager().registerEvents(seerCommand, this); // Register SeerCommand as a listener (if it handles events)
        getServer().getPluginManager().registerEvents(voteCommand, this); // Register VoteCommand as a listener (if it handles events)

    }


    @Override
    public void onDisable() {

    }
}
