package com.spectrasonic.Abracadabra.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.spectrasonic.Abracadabra.Main;
import com.spectrasonic.Abracadabra.Utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("abracadabra|abra")
public class AbracadabraCommand extends BaseCommand {

    private final Main plugin;

    public AbracadabraCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Subcommand("game start")
    @CommandPermission("abracadabra.admin")
    @Description("Inicia el juego en una ronda específica")
    @Syntax("<round>")
    @CommandCompletion("1|2|3")
    public void onGameStart(CommandSender sender, @Values("1|2|3") int round) {
        if (plugin.getGameManager().isGameRunning()) {
            MessageUtils.sendMessage(sender, "<red>El juego ya está en curso.</red>");
            return;
        }

        plugin.getGameManager().startGame(round);
        MessageUtils.sendMessage(sender, "<green>¡Juego Iniciado en Ronda <white>" + round + "</white>!</green>");

        if (sender instanceof Player) {
            Player playerStart = (Player) sender;
            playerStart.performCommand("id false");
            playerStart.performCommand("gamerule keepInventory true");
        }
    }

    @Subcommand("game stop")
    @CommandPermission("abracadabra.admin")
    @Description("Detiene el juego")
    public void onGameStop(CommandSender sender) {
        if (!plugin.getGameManager().isGameRunning()) {
            MessageUtils.sendMessage(sender, "<red>El juego no está en curso.</red>");
            return;
        }

        plugin.getGameManager().stopGame();
        MessageUtils.sendMessage(sender, "<red>Juego Detenido</red>");

        if (sender instanceof Player) {
            Player playerStop = (Player) sender;
            playerStop.performCommand("id true");
            playerStop.performCommand("gamerule keepInventory false");
        }
    }

    @Subcommand("reload")
    @CommandPermission("abracadabra.admin")
    @Description("Recarga la configuración del plugin")
    public void onReload(CommandSender sender) {
        plugin.reload();
        MessageUtils.sendMessage(sender, "<green>Configuración recargada correctamente.</green>");
    }

    @Default
    @HelpCommand
    public void onHelp(CommandSender sender) {
        MessageUtils.sendMessage(sender, "<yellow>Comandos disponibles:</yellow>");
        MessageUtils.sendMessage(sender, "<gray>- /abracadabra game start <round></gray> <white>- Inicia el juego en una ronda específica (1, 2 o 3)</white>");
        MessageUtils.sendMessage(sender, "<gray>- /abracadabra game stop</gray> <white>- Detiene el juego</white>");
        MessageUtils.sendMessage(sender, "<gray>- /abracadabra reload</gray> <white>- Recarga la configuración</white>");
    }
}