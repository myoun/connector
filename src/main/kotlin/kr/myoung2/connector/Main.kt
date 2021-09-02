package kr.myoung2.connector

import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.channel.ServerChannel
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.interaction.SlashCommand
import org.javacord.api.interaction.SlashCommandOption
import org.javacord.api.interaction.SlashCommandOptionType
import org.json.JSONObject
import java.io.File
import java.util.logging.Logger

val logger = Logger.getLogger("Connector")

fun main() {
    val token = File("token.txt").readText()
    val connector = Connector(token)
    connector.init()
}

class Connector(token:String) {
    var api : DiscordApi
        private set

    val share = mutableListOf<Long>()

    init { api = DiscordApiBuilder().setToken(token).login().join() }

    fun init() {
        api.addMessageCreateListener{ event ->
            if (event.channel !is ServerTextChannel) return@addMessageCreateListener
            val text = "[${event.server.get().name}/${event.serverTextChannel.get().name}] ${event.messageAuthor.discriminatedName} : ${event.messageContent}"
            if (event.messageAuthor.isBotUser) return@addMessageCreateListener
            for (sh in share) {
                if (event.channel.id == sh) continue
                (api.getChannelById(sh).get() as TextChannel).sendMessage(text)
            }
        }
        var have = false
        for (cmd in api.globalSlashCommands.get()) {
            if (cmd.name == "rc") {
                have = true
                break
            }
        }
        if (!have) {
            SlashCommand.with("rc", "register channel")
                .addOption(SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "channel", "channel", true))
                .createGlobal(api).join()
        }

        api.addSlashCommandCreateListener{ event ->
            val interaction = event.slashCommandInteraction
            if (interaction.commandName == "rc") {
                val channel = interaction.firstOptionChannelValue.get().id
                share.add(channel)
                interaction.createImmediateResponder()
                    .setContent("Channel <#$channel> Registered")
                    .setFlags(MessageFlag.UNKNOWN)
                    .respond()

            }
        }
    }
}

/**
 * 1. API 얻기
 * 2.
 */