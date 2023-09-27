package de.vepexlegit.playertracker;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

@Mod(modid = PlayerTracker.MODID, version = PlayerTracker.VERSION)
public class PlayerTracker {
    public static final String MODID = "playertracker";
    public static final String VERSION = "1.0";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static Minecraft mc = Minecraft.getMinecraft();
    private EntityPlayer targetPlayer;
    private long lastMessageTime = 0L;
    private final long messageInterval = 2000L;

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityPlayer && event.world.isRemote) {
            targetPlayer = (EntityPlayer) event.entity;
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Post event) {
        if (targetPlayer != null && event.entityPlayer != null && event.entityPlayer != mc.thePlayer) {
            double distance = event.entityPlayer.getDistanceSqToEntity(targetPlayer);
            if (distance < 25.0D) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastMessageTime >= messageInterval) {
                    String message = EnumChatFormatting.RED + "[Player Tracker] " + EnumChatFormatting.RESET +
                            "Игрок " + EnumChatFormatting.GREEN + targetPlayer.getDisplayName() + EnumChatFormatting.RESET +
                            " рядом с вами на координатах X:" + targetPlayer.posX + " Y:" + targetPlayer.posY + " Z:" + targetPlayer.posZ;
                    mc.thePlayer.addChatMessage(new ChatComponentText(message));
                    lastMessageTime = currentTime;
                }
            }
        }
    }
}
