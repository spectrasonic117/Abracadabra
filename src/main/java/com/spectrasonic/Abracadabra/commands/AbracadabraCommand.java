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

    @Subcommand("game")
    @CommandPermission("abracadabra.admin")
    @Description("Controla el estado del juego")
    @Syntax("<start|stop>")
    @CommandCompletion("start|stop")
    public void onGame(CommandSender sender, String action) {
        switch (action.toLowerCase()) {
            case "start":
                if (plugin.getGameManager().isGameRunning()) {
                    MessageUtils.sendMessage(sender, "<red>El juego ya está en curso.</red>");
                    return;
                }
                plugin.getGameManager().startGame();
                MessageUtils.sendMessage(sender, "<green>¡Juego Iniciado!</green>");
                Player playerStart = (Player) sender;
                playerStart.performCommand("id false");
                playerStart.performCommand("gamerule keepInventory true");
                break;
            case "stop":
                if (!plugin.getGameManager().isGameRunning()) {
                    MessageUtils.sendMessage(sender, "<red>El juego no está en curso.</red>");
                    return;
                }
                plugin.getGameManager().stopGame();
                MessageUtils.sendMessage(sender, "<red>Juego Detenido");
                Player playerStop = (Player) sender;
                playerStop.performCommand("id true");
                playerStop.performCommand("gamerule keepInventory false");
                break;
            default:
                MessageUtils.sendMessage(sender, "<red>Acción desconocida. Usa 'start' o 'stop'.</red>");
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
        MessageUtils.sendMessage(sender, "<gray>- /abracadabra game start</gray> <white>- Inicia el juego</white>");
        MessageUtils.sendMessage(sender, "<gray>- /abracadabra game stop</gray> <white>- Detiene el juego</white>");
        MessageUtils.sendMessage(sender, "<gray>- /abracadabra reload</gray> <white>- Recarga la configuración</white>");
    }
}
