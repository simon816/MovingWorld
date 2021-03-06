package darkevilmac.movingworld.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MovingWorldMessageToMessageCodec extends FMLIndexedMessageToMessageCodec<MovingWorldMessage> {

    private int index;

    public MovingWorldMessageToMessageCodec() {
        index = 1;
        addDiscriminator(ChunkBlockUpdateMessage.class);
        addDiscriminator(FarInteractMessage.class);
        addDiscriminator(MovingWorldClientActionMessage.class);
        addDiscriminator(RequestMovingWorldDataMessage.class);
        addDiscriminator(TileEntitiesMessage.class);
        addDiscriminator(ConfigMessage.class);
    }

    public FMLIndexedMessageToMessageCodec<MovingWorldMessage> addDiscriminator(Class<? extends MovingWorldMessage> type) {
        FMLIndexedMessageToMessageCodec<MovingWorldMessage> ret = super.addDiscriminator(index, type);
        index++;
        return ret;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, MovingWorldMessage msg, ByteBuf target) throws Exception {
        msg.encodeInto(ctx, target, FMLCommonHandler.instance().getSide());
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, MovingWorldMessage msg) {
        EntityPlayer player;
        switch (FMLCommonHandler.instance().getEffectiveSide()) {
            case CLIENT:
                player = this.getClientPlayer();
                msg.decodeInto(ctx, source, player, FMLCommonHandler.instance().getSide());
                break;
            case SERVER:
                player = getServerPlayer(ctx);
                msg.decodeInto(ctx, source, player, FMLCommonHandler.instance().getSide());
                break;
        }
    }

    @SideOnly(Side.CLIENT)
    private EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    private EntityPlayer getServerPlayer(ChannelHandlerContext ctx) {
        INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
        return ((NetHandlerPlayServer) netHandler).playerEntity;
    }
}
