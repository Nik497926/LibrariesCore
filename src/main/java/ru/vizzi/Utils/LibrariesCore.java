package ru.vizzi.Utils;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import com.google.common.util.concurrent.ListenableFutureTask;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.Validate;
import ru.justagod.cutter.GradleSide;
import ru.justagod.cutter.GradleSideOnly;
import ru.vizzi.Utils.databases.DatabaseManager;
import ru.vizzi.Utils.eventhandler.EventLoader;
import ru.vizzi.Utils.eventhandler.RegistryEvent;
import ru.vizzi.Utils.obf.IgnoreObf;
import ru.vizzi.Utils.resouces.CoreAPI;
import ru.vizzi.Utils.resouces.PreLoadableResourceManager;
import ru.vizzi.Utils.resouces.TextureLoader;

import javax.xml.crypto.Data;

@IgnoreObf
@Mod(modid = LibrariesCore.MODID, name = LibrariesCore.MODNAME, version = LibrariesCore.VERSION)
public class LibrariesCore {

	@Instance(LibrariesCore.MODID)
	public static LibrariesCore instance;
	
	@GradleSideOnly(GradleSide.CLIENT)
	public TextureLoader textureLoader;
	
	public static final String MODID = "LibrariesCore";
	public static final String MODNAME = "LibrariesCore";
	public static final String VERSION = "1.0.0";
	private boolean isClient = FMLCommonHandler.instance().getSide().isClient();
	public static final Logger logger = new Logger(MODID);

	private final Thread currentThread = Thread.currentThread();
	private final Queue queue = Queues.newArrayDeque();
	

	@EventHandler
	   public void preInit(FMLPreInitializationEvent event) {
		LibrariesConfig config = LibrariesConfig.getInstance();
		config.load();
		if(config.DEBUG){
			logger.setDebug(true);
		}

		SyncResultHandler.setMainThreadExecutor(this::runUsingMainThread);
	      if(isClient) {
	    	  textureLoader = new TextureLoader();
	    	  CoreAPI.init();
	      };
		EventLoader eventLoader = new EventLoader();
		eventLoader.onPreInit(event);
		FMLCommonHandler.instance().bus().register(this);
	      
	   }
	@EventHandler
	   public void Init(FMLInitializationEvent event) {
	
	   }
	@EventHandler
	   public void postInit(FMLPostInitializationEvent event) {
		 if(this.isClient) {
			 MinecraftForge.EVENT_BUS.post(new EventLoadResource());
		 }
		 try {

			 Class.forName("com.mysql.jdbc.Driver");
			 DatabaseManager.init();
		 } catch (Exception e){

		 }
	    
	      
	   }

	@SubscribeEvent
	public void serverUpdate(TickEvent.ServerTickEvent e){
		if(e.phase == TickEvent.Phase.START) {
			synchronized (this.queue)
			{
				while (!this.queue.isEmpty())
				{
					((FutureTask)this.queue.poll()).run();
				}
			}
		}
	}


	   @EventHandler
	   public void serverStart(FMLServerStartingEvent e){

	   }

	   @EventHandler
	   public void stopServer(FMLServerStoppingEvent e){
		if(DatabaseManager.isInit()) {
			DatabaseManager.shutdown();
		}
	   }

	@SuppressWarnings("unchecked")
    public <T> ListenableFuture<T> runUsingMainThread(Runnable runnable) {
		if(isClient) {
			return (ListenableFuture<T>) Minecraft.getMinecraft().func_152344_a(runnable);
		} else {
			return runUsingMainThreadServer(runnable);
		}
    }

    @SuppressWarnings("unchecked")
    public <T> ListenableFuture<T> runUsingMainThread(Callable<T> runnable) {
		if(isClient) {
			return (ListenableFuture<T>) Minecraft.getMinecraft().func_152343_a(runnable);
		} else {
			return runUsingMainThreadServer(runnable);
		}
    }



	public boolean getCurrentThread()
	{
		return Thread.currentThread() == this.currentThread;
	}


	private ListenableFuture runUsingMainThreadServer(Callable callable)
	{
		Validate.notNull(callable);

		if (!this.getCurrentThread())
		{
			ListenableFutureTask listenablefuturetask = ListenableFutureTask.create(callable);
			synchronized (this.queue)
			{
				this.queue.add(listenablefuturetask);
				return listenablefuturetask;
			}
		}
		else
		{
			try
			{
				return Futures.immediateFuture(callable.call());
			}
			catch (Exception exception)
			{
				return Futures.immediateFailedCheckedFuture(exception);
			}
		}
	}

	private ListenableFuture runUsingMainThreadServer(Runnable runnable)
	{
		Validate.notNull(runnable);
		return this.runUsingMainThreadServer(Executors.callable(runnable));
	}
}
