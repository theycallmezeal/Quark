/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 * 
 * File Created @ [26/03/2016, 21:53:17 (GMT)]
 */
package vazkii.quark.vanity.feature;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.aurelienribon.tweenengine.Tween;
import vazkii.quark.base.client.gui.GuiButtonTranslucent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.vanity.client.emotes.EmoteCheer;
import vazkii.quark.vanity.client.emotes.EmoteClap;
import vazkii.quark.vanity.client.emotes.EmoteFacepalm;
import vazkii.quark.vanity.client.emotes.EmoteHeadbang;
import vazkii.quark.vanity.client.emotes.EmoteNo;
import vazkii.quark.vanity.client.emotes.EmotePoint;
import vazkii.quark.vanity.client.emotes.EmoteSalute;
import vazkii.quark.vanity.client.emotes.EmoteShrug;
import vazkii.quark.vanity.client.emotes.EmoteWave;
import vazkii.quark.vanity.client.emotes.EmoteYes;
import vazkii.quark.vanity.client.emotes.base.EmoteHandler;
import vazkii.quark.vanity.client.emotes.base.ModelAccessor;
import vazkii.quark.vanity.client.gui.GuiButtonEmote;
import vazkii.quark.vanity.command.CommandEmote;

public class EmoteSystem extends Feature {

	private static final int EMOTE_BUTTON_START = 1800;
	static boolean emotesVisible = false;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient(FMLPreInitializationEvent event) {
		Tween.registerAccessor(ModelBiped.class, new ModelAccessor());

		MinecraftForge.EVENT_BUS.register(new EmoteHandler.TickHandler());
		
		EmoteHandler.emoteMap.put("wave", EmoteWave.class);
		EmoteHandler.emoteMap.put("salute", EmoteSalute.class);
		EmoteHandler.emoteMap.put("yes", EmoteYes.class);
		EmoteHandler.emoteMap.put("no", EmoteNo.class);
		EmoteHandler.emoteMap.put("cheer", EmoteCheer.class);
		EmoteHandler.emoteMap.put("clap", EmoteClap.class);
		EmoteHandler.emoteMap.put("point", EmotePoint.class);
		EmoteHandler.emoteMap.put("shrug", EmoteShrug.class);
		EmoteHandler.emoteMap.put("facepalm", EmoteFacepalm.class);
		EmoteHandler.emoteMap.put("headbang", EmoteHeadbang.class);
	}
	
	@Override
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandEmote());
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void initGui(GuiScreenEvent.InitGuiEvent.Post event) {
		GuiScreen gui = event.getGui();
		if(gui instanceof GuiChat) {
			List<GuiButton> list = event.getButtonList();
			list.add(new GuiButtonTranslucent(EMOTE_BUTTON_START, gui.width - 105, gui.height - 40, 105, 20, I18n.format("quark.gui.emotes")));
			
			int size = EmoteHandler.emoteMap.size() - 1;
			int i = 0;
			for(String key : EmoteHandler.emoteMap.keySet()) {
				int x = gui.width - 105 + (i % 2) * 55;
				int y = gui.height - 105 - 55 * (size / 2 - (i / 2));
				
				GuiButton button = new GuiButtonEmote(EMOTE_BUTTON_START + i + 1, x, y, key);
				button.visible = emotesVisible;
				button.enabled = emotesVisible;
				list.add(button);
				i++;
			}
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void performAction(GuiScreenEvent.ActionPerformedEvent.Pre event) {
		GuiButton button = event.getButton();
		
		if(button.id == EMOTE_BUTTON_START) {
			GuiScreen gui = event.getGui();
			List<GuiButton> list = event.getButtonList();
			
			for(GuiButton b : list)
				if(b instanceof GuiButtonEmote) {
					b.visible = !b.visible;
					b.enabled = !b.enabled;
				}
			
			emotesVisible = !emotesVisible;
		} else if(button instanceof GuiButtonEmote) {
			String emote = ((GuiButtonEmote) button).emote;
			Minecraft.getMinecraft().thePlayer.sendChatMessage("/emote " + emote);
		}
	}
	
	@Override
	public boolean hasSubscriptions() {
		return FMLCommonHandler.instance().getSide().isClient();
	}
	
}
