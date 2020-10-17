package com.useful.ucars.util;

import com.useful.ucars.UCars;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UEntityMeta {

    private static final Map<UUID, Object> entityMetaObjects = new ConcurrentHashMap<>(100, 0.75f, 2);
    private static final Map<UUID, WeakReference<Entity>> entityObjects = new ConcurrentHashMap<>(100, 0.75f, 2);

    public static void cleanEntityObjs(UCars uCars) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(uCars, () -> {
            for (Map.Entry<UUID, WeakReference<Entity>> entry : entityObjects.entrySet()) {
                UUID entID = entry.getKey();
                WeakReference<Entity> val = entry.getValue();
                if (val == null || val.get() == null) {
                    entityObjects.remove(entID);
                    Object metaObj = entityMetaObjects.remove(entID); //Entity no longer exists
                    if (metaObj != null) {
                        UMeta.removeAllMeta(metaObj);
                    }
                }
            }
            UMeta.clean();
        }, 1180L, 1180L); //Every 59 sec


		/*Bukkit.getScheduler().runTask(ucars.plugin, new Runnable(){

			@Override
			public void run() {
				final List<Entity> allEntities = new ArrayList<Entity>();
				for(World w:Bukkit.getWorlds()){
					allEntities.addAll(w.getEntities());
				}
				Bukkit.getScheduler().runTaskAsynchronously(ucars.plugin, new Runnable(){

					@Override
					public void run() {
						for(final Entity e:new ArrayList<Entity>(entityObjs.values())){
							if(e.isDead() && !e.isValid()){
								synchronized(entityMetaObjs){
									Bukkit.getScheduler().runTaskLaterAsynchronously(ucars.plugin, new Runnable(){

										@Override
										public void run() {
											entityObjs.remove(e.getUniqueId());
											entityMetaObjs.remove(e.getUniqueId());
											return;
										}}, 100l);
								}
							}
						}
						mainLoop: for(final UUID entID:new ArrayList<UUID>(entityMetaObjs.keySet())){
							for(Entity e:allEntities){
								if(e.getUniqueId().equals(entID)){
									continue mainLoop;
								}
							}
							Bukkit.getScheduler().runTaskLaterAsynchronously(ucars.plugin, new Runnable(){

								@Override
								public void run() {
									Object o = entityMetaObjs.get(entID);
									entityMetaObjs.remove(entID);
									if(o != null){
										UMeta.removeAllMeta(o);
									}
									return;
								}}, 100l);
						}
					}});
				return;
			}});*/
    }

    private static void setEntityObj(Entity entity) {
        if (entity instanceof Player) {
            return;
        }
        entityObjects.put(entity.getUniqueId(), new WeakReference<>(entity));
    }

    private static void delEntityObj(Entity entity) {
        if (entity instanceof Player) {
            return;
        }
        entityObjects.remove(entity.getUniqueId());
    }

    public static void printOutMeta(Entity entity) {
        StringBuilder sb = new StringBuilder();
        Map<String, List<MetadataValue>> metas = UMeta.getAllMeta(getMetaObj(entity));
        for (String key : new ArrayList<>(metas.keySet())) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(key);
        }
        Bukkit.broadcastMessage(entity.getUniqueId() + ": " + sb.toString());
    }

    public static void removeAllMeta(Entity entity) {
        Object o = entityMetaObjects.get(entity.getUniqueId());
        entityMetaObjects.remove(entity.getUniqueId());
        delEntityObj(entity);
        /*entityObjs.put(entity.getUniqueId(), entity);*/
        if (o != null) {
            UMeta.removeAllMeta(o);
        }
    }

    private static Object getMetaObj(Entity entity) {
        if (entity == null) {
            return null;
        }
        /*entityObjs.put(entity.getUniqueId(), entity);*/
        Object obj = entityMetaObjects.get(entity.getUniqueId());
        if (obj == null) {
            synchronized (USchLocks.getMonitor("newMetaObjMonitor" + entity.getUniqueId())) {
                obj = entityMetaObjects.get(entity.getUniqueId());
                if (obj == null) {
                    obj = new Object();
                    entityMetaObjects.put(entity.getUniqueId(), obj);
                    setEntityObj(entity);
                }
            }
        }
        return obj;
    }

    public static void setMetadata(Entity entity, String metaKey, MetadataValue value) {
        /*entityObjs.put(entity.getUniqueId(), entity);*/
        setEntityObj(entity);
        UMeta.getMeta(getMetaObj(entity), metaKey).add(value);
    }

    public static List<MetadataValue> getMetadata(Entity entity, String metaKey) {
        /*entityObjs.put(entity.getUniqueId(), entity);*/
        return UMeta.getAllMeta(getMetaObj(entity)).get(metaKey);
    }

    public static boolean hasMetadata(Entity entity, String metaKey) {
        /*entityObjs.put(entity.getUniqueId(), entity);*/
        return UMeta.getAllMeta(getMetaObj(entity)).containsKey(metaKey);
    }

    public static void removeMetadata(Entity entity, String metaKey) {
        /*entityObjs.put(entity.getUniqueId(), entity);*/
        UMeta.removeMeta(getMetaObj(entity), metaKey);
    }
}
