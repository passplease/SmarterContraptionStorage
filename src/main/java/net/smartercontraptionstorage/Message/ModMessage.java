package net.smartercontraptionstorage.Message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.smartercontraptionstorage.Utils;

import java.util.HashSet;
import java.util.function.Supplier;

public abstract class ModMessage {
    private static int id = 0;

    public static SimpleChannel INSTANCE;

    public static final HashSet<ModMessage> MESSAGES = new HashSet<>();

    public static final String MESSAGE = "menu_level_packet";

    public static final String VERSION = "1.0";

    public abstract void toBytes(FriendlyByteBuf buffer);

    public abstract <T extends ModMessage> T fromBytes(FriendlyByteBuf buffer);

    public abstract void handle(Supplier<NetworkEvent.Context> context);

    public static boolean clientAcceptedVersions(String version) {
        return VERSION.equals(version);
    }

    public static boolean serverAcceptedVersions(String version) {
        return VERSION.equals(version);
    }

    public static int id(){
        return id++;
    }

    public void sendToClient(ServerPlayer player){
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), this);
    }

    public static void register(ModMessage message){
        MESSAGES.add(message);
    }

    public static void registerMessages(){
        INSTANCE = NetworkRegistry.ChannelBuilder
                .named(Utils.asResources(MESSAGE))
                .networkProtocolVersion(() -> VERSION)
                .clientAcceptedVersions(ModMessage::clientAcceptedVersions)
                .serverAcceptedVersions(ModMessage::serverAcceptedVersions)
                .simpleChannel();
        for (ModMessage message : MESSAGES) {
            INSTANCE.messageBuilder(message.getClass(), ModMessage.id())
                    .decoder(message::fromBytes)
                    .encoder(ModMessage::toBytes)
                    .consumerMainThread(ModMessage::handle)
                    .add();
        }
    }
}
