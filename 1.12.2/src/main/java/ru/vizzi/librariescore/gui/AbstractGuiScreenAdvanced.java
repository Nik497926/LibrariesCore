package ru.vizzi.librariescore.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import ru.vizzi.librariescore.gui.drawmodule.ScaleGui;
import ru.vizzi.librariescore.resouces.CoreAPI;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public abstract class AbstractGuiScreenAdvanced extends GuiScreen {

    public ArrayList<GuiButtonNew> guiButtonsList = new ArrayList<>();
    protected float minAspect;

    public AbstractGuiScreenAdvanced() {
        this.minAspect = ScaleGui.FULL_HD;
        this.mc = Minecraft.getMinecraft();
    }

    public AbstractGuiScreenAdvanced(float minAspect) {
        this.minAspect = minAspect;
        this.mc = Minecraft.getMinecraft();
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        this.mc = mc;
        this.fontRenderer = mc.fontRenderer;
        this.initGui();
    }

    @Override
    public void initGui() {
        ScaleGui.update(minAspect);
		CoreAPI.isDefaultScale = false;
        this.width = mc.displayWidth;
        this.height = mc.displayHeight;
        this.guiButtonsList.clear();
    }

    @Override
    public void drawDefaultBackground() {
        float f = (float)(-1072689136 >> 24 & 255) / 255.0F;
        float f1 = (float)(-1072689136 >> 16 & 255) / 255.0F;
        float f2 = (float)(-1072689136 >> 8 & 255) / 255.0F;
        float f3 = (float)(-1072689136 & 255) / 255.0F;
        float f4 = (float)(-804253680 >> 24 & 255) / 255.0F;
        float f5 = (float)(-804253680 >> 16 & 255) / 255.0F;
        float f6 = (float)(-804253680 >> 8 & 255) / 255.0F;
        float f7 = (float)(-804253680 & 255) / 255.0F;
        glDisable(GL_TEXTURE_2D);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        glShadeModel(GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION);
        builder.color(f1,f2,f3,f);
        builder.pos(this.width, 0, this.zLevel);
        builder.pos(0, 0, this.zLevel);
        builder.color(f5, f6, f7, f4);
        builder.pos(0, this.height, this.zLevel);
        builder.pos(this.width, this.height, this.zLevel);
        tessellator.draw();

        glShadeModel(GL_FLAT);
        glEnable(GL_TEXTURE_2D);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0D, width, height, 0.0D, 0.0D, 3000.0D);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(0.0F, 0.0F, -2000.0F);

        glEnable(GL_BLEND);
        glAlphaFunc(GL_GREATER, 0.0001f);
        glDisable(GL_ALPHA_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
    }

    protected void drawButtons(int mouseX, int mouseY, float partialTick) {
       
        int k;

        for (k = 0; k < this.buttonList.size(); ++k)
        {
            ((GuiButtonNew)this.guiButtonsList.get(k)).drawButton(this.mc, mouseX, mouseY, partialTick);
        }
        
        
        for (k = 0; k < this.buttonList.size(); ++k)
        {
            ((GuiButton)this.buttonList.get(k)).drawButton(this.mc, mouseX, mouseY, partialTick);
        }

        for (k = 0; k < this.labelList.size(); ++k)
        {
            ((GuiLabel)this.labelList.get(k)).drawLabel(this.mc, mouseX, mouseY);
        }
        
    }

    @Override
    public void onGuiClosed() {
     CoreAPI.isDefaultScale = true;
    }

    public void scrollInput(int mouseX, int mouseY, int d) { }
}
