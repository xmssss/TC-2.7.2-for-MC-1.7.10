package com.khorn.terraincontrol.forge.events;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.khorn.terraincontrol.TerrainControl;
import com.khorn.terraincontrol.configuration.WorldConfig;
import com.khorn.terraincontrol.configuration.io.FileSettingsReader;
import com.khorn.terraincontrol.configuration.standard.WorldStandardValues;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.event.GuiOpenEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WorldCreationMenuHandler implements IGuiHandler
{	 
	boolean selecting;
	
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void openGui(GuiOpenEvent event)
    {       
        if (event.gui instanceof GuiCreateWorld && !selecting)
        {
            event.gui = new GuiSelectCreateWorldMode();
        }        
        if (event.gui instanceof GuiSelectWorld)
        {
            event.gui = new MCWGuiSelectWorld(new GuiMainMenu());
        }        
        selecting = false;
    }
    
    public void registerKeybindings() {}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@SideOnly(Side.CLIENT)
    public class MCWGuiSelectWorld extends GuiSelectWorld
    {
		public MCWGuiSelectWorld(GuiScreen p_i1054_1_)
		{
			super(p_i1054_1_);
			// TODO Auto-generated constructor stub
		}
				
	    public GuiYesNo askDeleteSettings(GuiYesNoCallback p_152129_0_, String worldName, int worldId)
	    {
	        String s1 = "Delete the TC world settings for '" + worldName + "' or keep them for re-use?";
	        String s2 = "If you are using a TC biome pack then keep the settings!";
	        String s3 = "Keep";
	        String s4 = "Delete";
	        GuiYesNo guiyesno = new GuiYesNo(p_152129_0_, s1, s2, s3, s4, worldId);
	        return guiyesno;
	    }
	    
		boolean derping;
		String worldName;
		@Override
	    public void confirmClicked(boolean ok, int worldId)
	    {
			// Delete existing TC/MCW settings
			
			if(!derping)
			{
				if(ok) // World is being deleted, ask to delete TC/MCW settings too
				{
					derping = true;
	
					worldName = this.func_146621_a(worldId);
					
					super.confirmClicked(ok, worldId);

					boolean bExists = false;
		            File TCWorldsDirectory = new File(TerrainControl.getEngine().getTCDataFolder().getAbsolutePath() + "/worlds");
		            if(TCWorldsDirectory.exists() && TCWorldsDirectory.isDirectory())
		            {
		            	for(File worldDir : TCWorldsDirectory.listFiles())
		            	{
		            		if(worldDir.isDirectory() && worldDir.getName().equals(worldName))
		            		{
		            			bExists = true;
		            			break;
		            		}					
		            	}
		            }
		            if(bExists)
		            {
						GuiYesNo guiyesno = askDeleteSettings(this, worldName, worldId);
						this.mc.displayGuiScreen(guiyesno);
		            }
				} else {
					super.confirmClicked(ok, worldId);
				}
			} else {
				derping = false;			
				if(!ok) // OK means "Keep" in this case
				{					
		            File TCWorldsDirectory = new File(TerrainControl.getEngine().getTCDataFolder().getAbsolutePath() + "/worlds");
		            if(TCWorldsDirectory.exists() && TCWorldsDirectory.isDirectory())
		            {
		            	for(File worldDir : TCWorldsDirectory.listFiles())
		            	{
		            		if(worldDir.isDirectory() && worldDir.getName().equals(worldName))
		            		{
		            			deleteRecursive(worldDir);
		            			break;
		            		}
		            	}
		        	}		            
				}
				this.mc.displayGuiScreen(new GuiSelectWorld(new GuiMainMenu()));
			}
	    }
    }
	
	public static void deleteRecursive(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	            	deleteRecursive(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}
	
	@SideOnly(Side.CLIENT)
    public class GuiSelectCreateWorldMode extends GuiScreen
    {	
		public GuiSelectCreateWorldMode() {}
		 
        @Override
        public void drawScreen(int x, int y, float f)
        {
            this.drawDefaultBackground();
            super.drawScreen(x, y, f);
        }
        
        GuiButton mcw;
        GuiButton vanilla;
        GuiButton cancel;
               
        @Override
        public void initGui()
        {
        	super.initGui();
            this.buttonList.add(this.mcw = new GuiButton(0, this.width / 2 - 100, this.height / 2 - 32, "TerrainControl"));
            this.buttonList.add(this.vanilla = new GuiButton(1, this.width / 2 - 100, this.height / 2 - 4, "Vanilla"));
            this.buttonList.add(this.cancel = new GuiButton(2, this.width / 2 - 100, this.height / 2 + 24, "Cancel"));            
        }
        
        @Override
        protected void actionPerformed(GuiButton button)
        {
        	super.actionPerformed(button);
        	if(button.enabled)
        	{
                if (button == this.cancel) // Cancel
                {
                    this.mc.displayGuiScreen(new GuiSelectWorld(new GuiMainMenu()));
                }
	            if (button == this.mcw)
	            {
	                //Main.packetHandler.sendToServer(...);
	                this.mc.displayGuiScreen(new MCWGuiCreateWorld(this));
	                if (this.mc.currentScreen == null)
	                {
	                    this.mc.setIngameFocus();
	                }
	            }
	            if (button == this.vanilla)
	            {
	            	selecting = true;
	                //Main.packetHandler.sendToServer(...);
	                this.mc.displayGuiScreen(new GuiCreateWorld(this));
	                if (this.mc.currentScreen == null)
	                {                	
	                    this.mc.setIngameFocus();
	                }
	            }
        	}
        }
    }
    
    @SideOnly(Side.CLIENT)
    public class MCWGuiCreateWorld extends GuiScreen implements GuiYesNoCallback
    {
        private GuiScreen sender;
        
        private GuiTextField txtWorldName;
        private GuiTextField txtSeed;
               
        private GuiButton btnavailableWorld1;
        private GuiButton btnavailableWorld2;
        private GuiButton btnavailableWorld3;
        private GuiButton btnavailableWorldPrev;
        private GuiButton btnavailableWorldNext;
        
        private GuiButton btnavailableWorldDelete1;
        private GuiButton btnavailableWorldDelete2;
        private GuiButton btnavailableWorldDelete3;
               
        private GuiButton btnCreateWorld;
        
        private String worldName;
        private boolean bBtnCreateNewWorldClicked;
        
        private String seed;
        private String worldName2;
        private final String[] field_146327_L = new String[] {"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};
        private final String __OBFID = "CL_00000689";
        
        private GuiButton btnBonusChest;
        private boolean bonusChest;
        
        private GuiButton btnGameMode;
        private int gameMode = 1;

        public MCWGuiCreateWorld(GuiScreen sender)
        {
            this.sender = sender;
            this.seed = "";
            this.worldName2 = I18n.format("selectWorld.newWorld", new Object[0]);
        }

        /**
         * Called from the main game loop to update the screen.
         */
        public void updateScreen()
        {
            this.txtWorldName.updateCursorCounter();
            this.txtSeed.updateCursorCounter();
        }

        int yoffset = 10;
        
        /**
         * Adds the buttons (and other controls) to the screen in question.
         */
        public void initGui()
        {
            Keyboard.enableRepeatEvents(true);
            
            this.buttonList.clear();            
            
            // World name
            this.txtWorldName = new GuiTextField(this.fontRendererObj, this.width / 2 - 155, yoffset + 57, 160, 20); //left, top, width, height
            this.txtWorldName.setFocused(true);
            this.txtWorldName.setText(this.worldName2);
            
            // Seed
            this.txtSeed = new GuiTextField(this.fontRendererObj, this.width / 2 - 155, yoffset + 115, 160, 20);
            this.txtSeed.setText(this.seed);

            int btnwidth = 136;
                        
            // Available worlds
            btnavailableWorld1 = new GuiButton(3, this.width / 2 + 15, yoffset + 55, btnwidth, 20, "None");            
            btnavailableWorld2 = new GuiButton(4, this.width / 2 + 15, yoffset + 55 + 24, btnwidth, 20, "None");
            btnavailableWorld3 = new GuiButton(5, this.width / 2 + 15, yoffset + 55 + 48, btnwidth, 20, "None");
            btnavailableWorldPrev = new GuiButton(6, this.width / 2 + 15, yoffset + 55 + 72, 78, 20, "Previous");
            btnavailableWorldNext = new GuiButton(7, this.width / 2 + 15 + 82, yoffset + 55 + 72, 78, 20, "Next");
            
            // Available worlds delete btns
            btnavailableWorldDelete1 = new GuiButton(8, this.width / 2 + 15 + btnwidth + 4, yoffset + 55, 20, 20, "X");
            btnavailableWorldDelete2 = new GuiButton(9, this.width / 2 + 15 + btnwidth + 4, yoffset + 55 + 24, 20, 20, "X");
            btnavailableWorldDelete3 = new GuiButton(10, this.width / 2 + 15 + btnwidth + 4, yoffset + 55 + 48, 20, 20, "X");
            
            this.buttonList.add(btnavailableWorld1);
            this.buttonList.add(btnavailableWorld2);
            this.buttonList.add(btnavailableWorld3);
            this.buttonList.add(btnavailableWorldPrev);
            this.buttonList.add(btnavailableWorldNext);
            
            this.buttonList.add(btnavailableWorldDelete1);
            this.buttonList.add(btnavailableWorldDelete2);
            this.buttonList.add(btnavailableWorldDelete3);
            
            FillAvailableWorlds();
          
            // Bonus chest
            this.buttonList.add(this.btnBonusChest = new GuiButton(11, this.width / 2 - 155, yoffset + 161, 160, 20, I18n.format("selectWorld.bonusItems", new Object[0])));
            
            this.btnBonusChest.displayString = I18n.format("selectWorld.bonusItems", new Object[0]) + " ";

            if (this.bonusChest)
            {
                this.btnBonusChest.displayString = I18n.format("selectWorld.bonusItems", new Object[0]) + " " + I18n.format("options.on", new Object[0]);
            } else {
                this.btnBonusChest.displayString = I18n.format("selectWorld.bonusItems", new Object[0]) + " " + I18n.format("options.off", new Object[0]);
            }
            
            this.buttonList.add(this.btnGameMode = new GuiButton(12, this.width / 2 + 15, yoffset + 161, 160, 20, I18n.format(this.gameMode == 0 ? "Creative" : this.gameMode == 1 ? "Survival" : this.gameMode == 2 ? "Adventure" : "Creative", new Object[0])));
            
            // Create / Cancel
            btnCreateWorld = new GuiButton(0, this.width / 2 - 155, this.height - 38, 160, 20, I18n.format("selectWorld.create", new Object[0]));
            this.buttonList.add(btnCreateWorld);
            this.buttonList.add(new GuiButton(1, this.width / 2 + 15, this.height - 38, 160, 20, I18n.format("gui.cancel", new Object[0])));
            
            this.updateWorldName(true);
        }
        
        HashMap<String,WorldConfig> worlds = new HashMap<String, WorldConfig>();
        String selectedWorldName = null;
        WorldConfig selectedWorldConfig = null;
        
        int pageNumber = 0;
        private void FillAvailableWorlds()
        {
            ArrayList<String> worldNames = new ArrayList<String>();
        	
            File TCWorldsDirectory = new File(TerrainControl.getEngine().getTCDataFolder().getAbsolutePath() + "/worlds");
            if(TCWorldsDirectory.exists() && TCWorldsDirectory.isDirectory())
            {
            	for(File worldDir : TCWorldsDirectory.listFiles())
            	{
            		if(worldDir.isDirectory())
            		{
            			worldNames.add(worldDir.getName());
            			if(!worlds.containsKey(worldDir.getName()))
            			{
                            File worldConfigFile = new File(worldDir, WorldStandardValues.WORLD_CONFIG_FILE_NAME);
                            WorldConfig worldConfig = new WorldConfig(new FileSettingsReader(worldDir.getName(), worldConfigFile), null, null);
                            worlds.put(worldDir.getName(), worldConfig);
            			}
            		}
            	}
        	}
            
            int pages = (int)Math.ceil(worldNames.size() / 3d);
            if(pageNumber > pages - 1)
            {
            	pageNumber = pages - 1;
            }
            int i = 0;
            
            btnavailableWorld1.displayString = "None";
            btnavailableWorld2.displayString = "None";
            btnavailableWorld3.displayString = "None";
            
            for(String worldName : worldNames)
            {
            	i += 1;
            	if(i == (pageNumber * 3) + 1)
            	{
            		btnavailableWorld1.displayString = worldName;
            	}
            	if(i == (pageNumber * 3) + 2)
            	{
            		btnavailableWorld2.displayString = worldName;
            	}
            	if(i == (pageNumber * 3) + 3)
            	{
            		btnavailableWorld3.displayString = worldName;
            	}
            }
            
        	btnavailableWorld1.enabled = !btnavailableWorld1.displayString.equals("None");
        	btnavailableWorld2.enabled = !btnavailableWorld2.displayString.equals("None");
        	btnavailableWorld3.enabled = !btnavailableWorld3.displayString.equals("None");

        	btnavailableWorldDelete1.enabled = !btnavailableWorld1.displayString.equals("None");
        	btnavailableWorldDelete2.enabled = !btnavailableWorld2.displayString.equals("None");
        	btnavailableWorldDelete3.enabled = !btnavailableWorld3.displayString.equals("None");
        	
            if(worldNames.size() > 3)
            {
            	btnavailableWorldPrev.enabled = true;
            	btnavailableWorldNext.enabled = true;
            } else {
            	btnavailableWorldPrev.enabled = false;
            	btnavailableWorldNext.enabled = false;            	
            }
            
            if(pageNumber == 0)
            {
            	btnavailableWorldPrev.enabled = false;	
            }
            if(pageNumber == pages -1)
            {
            	btnavailableWorldNext.enabled = false;
            }
        }

        private void previousPage()
        {
        	pageNumber -= 1;
        	if(pageNumber < 0)
        	{
        		pageNumber = 0;
        	}
        	FillAvailableWorlds();
        }

        private void nextPage()
        {
        	pageNumber += 1;
        	FillAvailableWorlds();
        }
        
        private void updateWorldName(boolean resetTextFields)
        {
            this.worldName = this.txtWorldName.getText().trim();
            
            btnCreateWorld.enabled = this.txtWorldName.getText().length() > 0;
            
            if(this.worldName.length() == 0)
            {
            	worldNameHelpText = "World name cannot be empty";
            } else {            
	            char[] achar = ChatAllowedCharacters.allowedCharacters;
	            int i = achar.length;
	
	            for (int j = 0; j < i; ++j)
	            {
	                char c0 = achar[j];
	                this.worldName = this.worldName.replace(c0, '_');
	            }
	
	            this.worldName = getCorrectWorldName(this.mc.getSaveLoader(), this.worldName);
	                        
	            boolean WorldNameExists = false;
	            if (this.mc.getSaveLoader().getWorldInfo(worldName) != null)
	            {
	            	worldNameHelpText = "Existing world will be deleted!";
	            } else {            	           	
	            	worldNameHelpText = "New world dir will be created";
	            }            
	           
	            ArrayList<String> worldNames = new ArrayList<String>();
	        	boolean usingPreset = false;
	            
				selectedWorldName = null;
				selectedWorldConfig = null;
	        	
	            File TCWorldsDirectory = new File(TerrainControl.getEngine().getTCDataFolder().getAbsolutePath() + "/worlds");
	            if(TCWorldsDirectory.exists() && TCWorldsDirectory.isDirectory())
	            {
	            	for(File worldDir : TCWorldsDirectory.listFiles())
	            	{
	            		if(worldDir.isDirectory())
	            		{
	            			if(worldName.equals(worldDir.getName()))
	            			{
	            				selectedWorldName = worldDir.getName();
	            				selectedWorldConfig = worlds.get(worldDir.getName());	            				
	            				
	            				usingPreset = true;
	            				break;
	            			}
	            		}
	            	}
	        	}
	            
	            if(usingPreset)
	            {
	            	worldNameHelpText2 = "Using existing settings";
	            	if(resetTextFields)
	            	{		                
	                    this.txtSeed.setText(selectedWorldConfig.worldSeed);
	                    this.seed = selectedWorldConfig.worldSeed;
	            	} 	            	
	            } else {
	            	worldNameHelpText2 = "Using default settings";
	            	
	            	if(resetTextFields)
	            	{		               
	                    this.txtSeed.setText("");
	                    this.seed = "";
	            	}
	            }
            }
        }

        String worldNameHelpText;
        String worldNameHelpText2;
        public String getCorrectWorldName(ISaveFormat anvilSaveConverter, String worldName)
        {
        	worldName = worldName.replaceAll("[\\./\"]", "_");
            String[] astring = field_146327_L;
            int i = astring.length;

            for (int j = 0; j < i; ++j)
            {
                String s1 = astring[j];

                if (worldName.equalsIgnoreCase(s1))
                {
                	worldName = "_" + worldName + "_";
                }
            }          

            return worldName;
        }

        /**
         * Called when the screen is unloaded. Used to disable keyboard repeat events
         */
        public void onGuiClosed()
        {
            Keyboard.enableRepeatEvents(false);
        }

        String worldNameToDelete = "";
        protected void actionPerformed(GuiButton button)
        {
            if (button.enabled)
            {
            	if (button.id == 3) // Available world 1 
            	{
            		if(btnavailableWorld1.displayString.length() > 0 && !btnavailableWorld1.displayString.equalsIgnoreCase("none"))
            		{
            			this.txtWorldName.setText(btnavailableWorld1.displayString);	
            		} else {
            			this.txtWorldName.setText("New World");
            		}
                    this.updateWorldName(true);
            	}
            	if (button.id == 4) // Available world 2 
            	{
            		if(btnavailableWorld2.displayString.length() > 0 && !btnavailableWorld2.displayString.equalsIgnoreCase("none"))
            		{
            			this.txtWorldName.setText(btnavailableWorld2.displayString);	
            		} else {
            			this.txtWorldName.setText("New World");
            		}
                    this.updateWorldName(true);
            	}
            	if (button.id == 5) // Available world 3 
            	{
            		if(btnavailableWorld3.displayString.length() > 0 && !btnavailableWorld3.displayString.equalsIgnoreCase("none"))
            		{
            			this.txtWorldName.setText(btnavailableWorld3.displayString);	
            		} else {
            			this.txtWorldName.setText("New World");
            		}
                    this.updateWorldName(true);
            	}
            	
            	if (button.id == 8) // Available world delete 1
            	{
            		if(btnavailableWorld1.displayString.length() > 0 && !btnavailableWorld1.displayString.equalsIgnoreCase("none"))
            		{
						GuiYesNo guiyesno = askDeleteSettings(this, btnavailableWorld1.displayString);
						worldNameToDelete = btnavailableWorld1.displayString.trim();
						this.mc.displayGuiScreen(guiyesno);
            		}
            	}
            	if (button.id == 9) // Available world delete 2
            	{
            		if(btnavailableWorld2.displayString.length() > 0 && !btnavailableWorld2.displayString.equalsIgnoreCase("none"))
            		{
						GuiYesNo guiyesno = askDeleteSettings(this, btnavailableWorld2.displayString);
						worldNameToDelete = btnavailableWorld2.displayString.trim();
						this.mc.displayGuiScreen(guiyesno);            			
            		}            		
            	}
            	if (button.id == 10) // Available world delete 3
            	{
            		if(btnavailableWorld3.displayString.length() > 0 && !btnavailableWorld3.displayString.equalsIgnoreCase("none"))
            		{
						GuiYesNo guiyesno = askDeleteSettings(this, btnavailableWorld3.displayString);
						worldNameToDelete = btnavailableWorld3.displayString.trim();
						this.mc.displayGuiScreen(guiyesno);
            		}	
            	}
            	
            	if (button.id == 6) // Previous 
            	{
            		previousPage();
            	}            	
            	if (button.id == 7) // Next 
            	{
            		nextPage();
            	}            	
                if (button.id == 1) // Cancel
                {
                    this.mc.displayGuiScreen(this.sender);
                }
                else if (button.id == 0) // Create new world
                {
                    //this.mc.displayGuiScreen((GuiScreen)null);

                    if (this.bBtnCreateNewWorldClicked)
                    {
                        return;
                    }

                    this.bBtnCreateNewWorldClicked = true;
                    long i = (new Random()).nextLong();
                    String s = this.txtSeed.getText().trim();

                    if (!MathHelper.stringNullOrLengthZero(s))
                    {
                        try
                        {
                            long j = Long.parseLong(s);

                            if (j != 0L)
                            {
                                i = j;
                            }
                        }
                        catch (NumberFormatException numberformatexception)
                        {
                            i = (long)s.hashCode();
                        }
                    }                    
                    
                    ISaveFormat isaveformat = this.mc.getSaveLoader();
                    isaveformat.flushCache();
                    isaveformat.deleteWorldDirectory(this.worldName);
                    
                    WorldType.parseWorldType("TerrainControl").onGUICreateWorldPress();

                    WorldSettings.GameType gametype = this.gameMode == 0 ? WorldSettings.GameType.CREATIVE : this.gameMode == 1 ? WorldSettings.GameType.SURVIVAL : this.gameMode == 2 ? WorldSettings.GameType.ADVENTURE : WorldSettings.GameType.CREATIVE;
                    WorldSettings worldsettings = new WorldSettings(i, gametype, true, false, WorldType.parseWorldType("TerrainControl"));
                    worldsettings.func_82750_a("TerrainControl");

                    if(this.bonusChest)
                    {
                    	worldsettings.enableBonusChest();
                	}
                    worldsettings.enableCommands();                    
                                        
                    // Clear existing pre-generator and structurecache data
                    // Do this here in the Forge layer instead of in common since this only applies to MCW atm.
                    File TCWorldsDirectory = new File(TerrainControl.getEngine().getTCDataFolder().getAbsolutePath() + "/worlds");
                    if(TCWorldsDirectory.exists() && TCWorldsDirectory.isDirectory())
                    {
                    	for(File worldDir : TCWorldsDirectory.listFiles())
                    	{
                    		if(worldDir.isDirectory())
                    		{
                    			if(worldName.equals(worldDir.getName()))
                    			{
                    				
                    				File StructureDataDirectory = new File(worldDir.getAbsolutePath() + "/StructureData");
                                    if (StructureDataDirectory.exists())
                                    {
                                    	deleteRecursive(StructureDataDirectory);
                                    }

                                    File structureDataFile = new File(worldDir.getAbsolutePath() + "/StructureData.txt");
                                    if (structureDataFile.exists())
                                    {
                                    	deleteRecursive(structureDataFile);
                                    }
                                    
                                    File nullChunksFile = new File(worldDir.getAbsolutePath() + "/NullChunks.txt");
                                    if (nullChunksFile.exists())
                                    {
                                    	deleteRecursive(nullChunksFile);
                                    }
                                    
                                    File spawnedStructuresFile = new File(worldDir.getAbsolutePath() + "/SpawnedStructures.txt");
                                    if (spawnedStructuresFile.exists())
                                    {
                                    	deleteRecursive(spawnedStructuresFile);
                                    }

                                    File chunkProviderPopulatedChunksFile = new File(worldDir.getAbsolutePath() + "/ChunkProviderPopulatedChunks.txt");
                                    if (chunkProviderPopulatedChunksFile.exists())
                                    {
                                    	deleteRecursive(chunkProviderPopulatedChunksFile);
                                    }

                                    File pregeneratedChunksFile = new File(worldDir.getAbsolutePath() + "/PregeneratedChunks.txt");
                                    if (pregeneratedChunksFile.exists())
                                    {
                                    	deleteRecursive(pregeneratedChunksFile);
                                    }                    				
                    				
                    				break;
                    			}
                    		}
                    	}
                	}                        
                    this.mc.launchIntegratedServer(this.worldName, this.txtWorldName.getText().trim(), worldsettings);
                }
	            if(button.id == 11)
	            {
	                this.bonusChest = !this.bonusChest;
	                if (this.bonusChest)
	                {
	                    this.btnBonusChest.displayString = I18n.format("selectWorld.bonusItems", new Object[0]) + " " + I18n.format("options.on", new Object[0]);
	                } else {
	                    this.btnBonusChest.displayString = I18n.format("selectWorld.bonusItems", new Object[0]) + " " + I18n.format("options.off", new Object[0]);
	                }
	            }                
	            if(button.id == 12)
	            {
	            	this.gameMode += 1;
	            	
	            	if(this.gameMode > 2)
	            	{
	            		this.gameMode = 0;	
	            	}	            	
                    this.btnGameMode.displayString = I18n.format(this.gameMode == 0 ? "Creative" : this.gameMode == 1 ? "Survival" : this.gameMode == 2 ? "Adventure" : "Creative", new Object[0]);
	            }                
            }
        }

		@Override
	    public void confirmClicked(boolean ok, int worldId)
	    {
			if(ok)
			{
	            File TCWorldsDirectory = new File(TerrainControl.getEngine().getTCDataFolder().getAbsolutePath() + "/worlds");
	            if(TCWorldsDirectory.exists() && TCWorldsDirectory.isDirectory())
	            {
	            	for(File worldDir : TCWorldsDirectory.listFiles())
	            	{
	            		if(worldDir.isDirectory() && worldDir.getName().equals(worldNameToDelete))
	            		{
	            			deleteRecursive(worldDir);
	            			break;
	            		}
	            	}
	        	}
	            
                ISaveFormat isaveformat = this.mc.getSaveLoader();
                isaveformat.flushCache();
                isaveformat.deleteWorldDirectory(worldNameToDelete);
			}
			this.mc.displayGuiScreen(new MCWGuiCreateWorld(new GuiSelectCreateWorldMode()));
	    }
		
	    public GuiYesNo askDeleteSettings(GuiYesNoCallback p_152129_0_, String worldName)
	    {
	        String s1 = "Delete the TC world settings for '" + worldName + "'?";
	        String s2 = "This will also delete any world (directory) named '" + worldName + "'";
	        String s3 = "Delete";
	        String s4 = "Cancel";
	        GuiYesNo guiyesno = new GuiYesNo(p_152129_0_, s1, s2, s3, s4, 0);
	        return guiyesno;
	    }
        
        /**
         * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
         */
        protected void keyTyped(char p_73869_1_, int p_73869_2_)
        {
            if (this.txtWorldName.isFocused())
            {
                this.txtWorldName.textboxKeyTyped(p_73869_1_, p_73869_2_);
                this.worldName2 = this.txtWorldName.getText();
            }
            else if (this.txtSeed.isFocused())
            {
                this.txtSeed.textboxKeyTyped(p_73869_1_, p_73869_2_);
                this.seed = this.txtSeed.getText();
            }            

            if (p_73869_2_ == 28 || p_73869_2_ == 156)
            {
                this.actionPerformed((GuiButton)this.buttonList.get(0));
            }
            
            this.updateWorldName(false);
        }

        /**
         * Called when the mouse is clicked.
         */
        protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_)
        {
            super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);

            this.txtSeed.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
            this.txtWorldName.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        }

        /**
         * Draws the screen and all the components in it.
         */
        public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_)
        {
            this.drawDefaultBackground();
            
            // Create new world title
            this.drawCenteredString(this.fontRendererObj, I18n.format("Create a new TC world", new Object[0]), this.width / 2, 20, -1);

            // World name
            this.drawString(this.fontRendererObj, I18n.format("selectWorld.enterName", new Object[0]), this.width / 2 - 155, yoffset + 42, -6250336);
            this.drawString(this.fontRendererObj, this.worldNameHelpText, this.width / 2 - 155,  yoffset + 82, -6250336);
            this.txtWorldName.drawTextBox();
            
            // Available worlds
            this.drawString(this.fontRendererObj, I18n.format("Existing world settings", new Object[0]), this.width / 2 + 20, yoffset + 42, -6250336);
            
            // Seed
            this.drawString(this.fontRendererObj, I18n.format("selectWorld.enterSeed", new Object[0]), this.width / 2 - 155, yoffset + 102, -6250336);
            this.drawString(this.fontRendererObj, I18n.format("selectWorld.seedInfo", new Object[0]), this.width / 2 - 155, yoffset + 140, -6250336);
            this.txtSeed.drawTextBox();

            super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        }

        public void func_146318_a(WorldInfo worldInfo)
        {
            this.worldName2 = I18n.format("selectWorld.newWorld.copyOf", new Object[] { worldInfo.getWorldName()});
            this.seed = worldInfo.getSeed() + "";
        }
    }	
}
