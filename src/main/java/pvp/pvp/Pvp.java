package pvp.pvp;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
public final class Pvp extends JavaPlugin implements Listener {


    private final Inventory inv;
    boolean isgame = false;
    private BossBar bossBar = null;
    private BossBar bossBar2 = null;
    Location l1 = null;
    Location l2 = null;
    String p1 = null;
    String p2 = null;
    float redsec = 0.0f;
    boolean isready = false;
    HashMap<UUID, Boolean> cooltime = new HashMap<>();
    ConsoleCommandSender consol = Bukkit.getConsoleSender();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        consol.sendMessage(ChatColor.AQUA + "[pvp2] 플러그인 활성화.");
        ItemStack item = new ItemStack(Material.LIGHT_BLUE_DYE,1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName("pvp권");
        item.setItemMeta(meta);
        ShapelessRecipe newrecipe = new ShapelessRecipe(new NamespacedKey(this, "pvp_ticket"),item).addIngredient(1,Material.DIAMOND_BLOCK).addIngredient(1,Material.GOLD_BLOCK).addIngredient(1,Material.EXPERIENCE_BOTTLE).addIngredient(1,Material.ELYTRA).addIngredient(1,Material.NETHERITE_BLOCK);
        getServer().addRecipe(newrecipe);




    }

    @Override
    public void onDisable() {
        getServer().removeRecipe(new NamespacedKey(this, "pvp_ticket"));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!cooltime.containsKey(e.getPlayer().getUniqueId())) {
            cooltime.put(e.getPlayer().getUniqueId(), true);
            e.getPlayer().discoverRecipe(new NamespacedKey(this, "pvp_ticket"));
        }


    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if (e.getPlayer().getWorld().getName().equalsIgnoreCase("fight")) {
            e.getPlayer().setHealth(0);
        }
    }

    @EventHandler
    public void onhand(PlayerItemHeldEvent e) {

        new BukkitRunnable() {
            @Override
            public void run() {
                if (e.getPlayer().getInventory().getItemInMainHand()!=null&&e.getPlayer().getInventory().getItemInMainHand().getType() == Material.LIGHT_BLUE_DYE&&e.getPlayer().getWorld().getName()!="fight") {
                    Player p = e.getPlayer();
                    assert p.getInventory().getItemInMainHand().getItemMeta() !=null;
                    if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase("pvp권")) {
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent("pvp권 사용 가능"));
                    }
                }
                cancel();
            }
        }.runTaskTimer(this,5L,1L);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(e.getPlayer().getWorld().getName().equalsIgnoreCase("fight"))
        {
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            {
                if(e.getClickedBlock().getType()==Material.ENDER_CHEST||e.getClickedBlock().getType()==Material.SHULKER_BOX)
                {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(ChatColor.RED+"이 월드에서는 셜커상자와 엔더상자를 사용할 수 없습니다");
                }
            }
        }
        else if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if (p.getInventory().getItemInMainHand() != null && p.getInventory().getItemInMainHand().getType() == Material.LIGHT_BLUE_DYE) {

                if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase("pvp권")) {
                    if (cooltime.get(e.getPlayer().getUniqueId())) {

                        p.getInventory().removeItem(p.getInventory().getItemInMainHand());
                        openInventory(p);
                        e.setCancelled(true);
                    } else {
                        p.sendMessage("쿨타임이 남았습니다.");
                    }
                }
            }

        }
    }


    public Pvp() {
        inv = Bukkit.createInventory(null, 27, "1vs1 PVP");
    }

    public void initializeItems() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        inv.clear();
        for (Player player : players) {
            String nickname = player.getName();
            ItemStack skullstack = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta skull = (SkullMeta) skullstack.getItemMeta();
            skull.setOwner(nickname);

            skullstack.setItemMeta(skull);
            inv.addItem(createGuiItem(skullstack, nickname));
        }

        for (int i = 0; i < 27 - players.size(); i++) {
            ItemStack stack = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
            inv.addItem(createGuiItem(stack, Integer.toString(i)));
        }

    }

    protected ItemStack createGuiItem(final ItemStack item, final String name, final String... lore) {

        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }


    public void openInventory(final HumanEntity ent) {
        initializeItems();
        ent.openInventory(inv);
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e)
    {
        if(e.getInventory().equals(inv))
        {
            ItemStack item = new ItemStack(Material.LIGHT_BLUE_DYE,1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("pvp권");
            item.setItemMeta(meta);
            e.getPlayer().getInventory().addItem(item);
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory() != inv) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) return;

        final Player p = (Player) e.getWhoClicked();


        if (e.isLeftClick() && e.getInventory().getItem(e.getRawSlot()).getType() != Material.LIGHT_GRAY_STAINED_GLASS_PANE)//사람 클릭
        {
            String name = e.getInventory().getItem(e.getRawSlot()).getItemMeta().getDisplayName();
            ItemStack item = new ItemStack(Material.LIGHT_BLUE_DYE,1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("pvp권");
            item.setItemMeta(meta);
            //사람이 있는지   없으면 아이템 돌려주기
            if (name.equalsIgnoreCase(p.getName())) {
                p.sendMessage("자기 자신을 선택할 수 없습니다");

                p.getInventory().addItem(item);
                p.closeInventory();
                return;
            }
            if (getServer().getPlayer(name) == null) {
                p.sendMessage(name + "님이 서버에 없습니다.");


                p.getInventory().addItem(item);
                p.closeInventory();
                return;
            }
            else if (!cooltime.get(getServer().getPlayer(name).getUniqueId())) {
                p.sendMessage(name + "님이 쿨타임이 남았습니다.");

                p.getInventory().addItem(item);
                p.closeInventory();
                return;
            }
            p.getInventory().remove(item);
            //갑옷 귀속저주
            /*
            if(getServer().getPlayer(name).getInventory().getHelmet()!=null)
            {
                ItemStack itemStack = getServer().getPlayer(name).getInventory().getHelmet();
                itemStack.addEnchantment(Enchantment.BINDING_CURSE,1);
                getServer().getPlayer(name).getInventory().setHelmet(itemStack);
            }
            if(getServer().getPlayer(name).getInventory().getChestplate()!=null)
            {
                if(getServer().getPlayer(name).getInventory().getChestplate().getType().equals(Material.ELYTRA))
                {
                    ItemStack itemStack = getServer().getPlayer(name).getInventory().getChestplate();
                    short du = 1;
                    itemStack.setDurability(du);
                    getServer().getPlayer(name).getInventory().setChestplate(itemStack);
                }
                else
                {
                    ItemStack itemStack = getServer().getPlayer(name).getInventory().getChestplate();
                    itemStack.addEnchantment(Enchantment.BINDING_CURSE,1);
                    getServer().getPlayer(name).getInventory().setChestplate(itemStack);
                }
            }
            if(getServer().getPlayer(name).getInventory().getLeggings()!=null)
            {
                ItemStack itemStack = getServer().getPlayer(name).getInventory().getLeggings();
                itemStack.addEnchantment(Enchantment.BINDING_CURSE,1);
                getServer().getPlayer(name).getInventory().setLeggings(itemStack);
            }
            if(getServer().getPlayer(name).getInventory().getBoots()!=null)
            {
                ItemStack itemStack = getServer().getPlayer(name).getInventory().getBoots();
                itemStack.addEnchantment(Enchantment.BINDING_CURSE,1);
                getServer().getPlayer(name).getInventory().setBoots(itemStack);
            }
            */

            p.closeInventory();
            if (!isgame && !isready) {
                p1 = p.getName();
                p2 = name;
                isgame = false;
                isready = true;  //여기부터 나가면 isready false

                redsec = 300;

                bossBar = Bukkit.createBossBar("준비시간", BarColor.BLUE, BarStyle.SOLID);
                Worldcreater();
                bossBar.addPlayer(getServer().getPlayer(p.getName()));

                bossBar2 = Bukkit.createBossBar("준비기간", BarColor.BLUE, BarStyle.SOLID);

                bossBar2.addPlayer(getServer().getPlayer(name));


                bossBar.setVisible(true);
                bossBar2.setVisible(true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (getServer().getPlayer(p.getName()) != null && getServer().getPlayer(name) != null) {
                            redsec -= 1;

                            bossBar.setProgress((float) redsec / 300);
                            bossBar2.setProgress((float) redsec / 300);

                            if (redsec == 0) {//여기부터 나가면 즉사
                                World world = getServer().getWorld("fight");
                                world.setDifficulty(Difficulty.PEACEFUL);
                                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                                world.setGameRule(GameRule.DO_MOB_SPAWNING, false);


                                world.getWorldBorder().setCenter(25, 25);
                                world.getWorldBorder().setSize(55);
                                bossBar.removePlayer(getServer().getPlayer(p.getName()));

                                bossBar2.removePlayer(getServer().getPlayer(name));
                                bossBar.setVisible(false);
                                bossBar2.setVisible(false);

                                //fight

                                l1 = getServer().getPlayer(p1).getLocation();
                                l2 = getServer().getPlayer(p2).getLocation();
                                getServer().getWorld("fight").getHighestBlockAt(0,0).setType(Material.STONE);
                                getServer().getWorld("fight").getHighestBlockAt(50,50).setType(Material.STONE);
                                getServer().getPlayer(p1).teleport(new Location(getServer().getWorld("fight"),0,getServer().getWorld("fight").getHighestBlockYAt(0,0)+1,0));
                                getServer().getPlayer(p2).teleport(new Location(getServer().getWorld("fight"),50,getServer().getWorld("fight").getHighestBlockYAt(50,50)+1,50));
                                getServer().getPlayer(p1).setGlowing(true);
                                getServer().getPlayer(p2).setGlowing(true);
                                isgame = true;
                                isready = false;

                                cancel();
                            }
                        }
                    }
                }.runTaskTimer(this,0L,20L);
                    //이제 텔포하고 배틀 시작


                    //싸워
            }
            else
            {
                p.sendMessage("이미 " + p1 + "님과 " + p2 + "님이 전투중입니다.");


                p.getInventory().addItem(item);
                p.closeInventory();
                return;
            }

        }
    }

    @EventHandler
    public void onInventoryClick ( final InventoryDragEvent e){
            if (e.getInventory().equals(inv)) {
                e.setCancelled(true);
            }
    }

    @EventHandler
    public void onPotal(PlayerPortalEvent e)
    {
        if(e.getPlayer().getWorld().getName().equalsIgnoreCase("fight"))
        {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDead (PlayerDeathEvent e)
    {
        if (e.getEntity().getName().equalsIgnoreCase(p1) && isgame&&!isready) {
            isready = true;

            consol.sendMessage(p2 + "님이 승리하였습니다.");
            getServer().getPlayer(p1).getEnderChest().clear();
            cooltime.put(getServer().getPlayer(p1).getUniqueId(), false);
            cooltime.put(getServer().getPlayer(p2).getUniqueId(), false);
            getServer().getPlayer(p2).sendMessage(ChatColor.AQUA+"승리하셨습니다.");
            getServer().getPlayer(p2).sendMessage(ChatColor.YELLOW + "5분 뒤 자동으로 마지막 장소로 이동합니다.");
            redsec = 300;
            BossBar bossBar = Bukkit.createBossBar("템 정리시간", BarColor.BLUE, BarStyle.SOLID);
            bossBar.addPlayer(getServer().getPlayer(p2));
            e.getEntity().getPlayer().setGlowing(false);
            getServer().getPlayer(p2).setGlowing(false);

            bossBar.setProgress(((float) redsec / 300));
            bossBar.setVisible(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    redsec -= 1;

                    bossBar.setProgress(((float) redsec / 300));
                    if (redsec == 0) {
                        isgame = false;
                        isready = false;
                        bossBar.removePlayer(getServer().getPlayer(p2));
                        bossBar.setVisible(false);

                        getServer().getPlayer(p2).teleport(l2);
                        String pp1 = p1;
                        String pp2 = p2;
                        p1 = null;
                        p2 = null;
                        l1 = null;
                        l2 = null;

                        getServer().getWorld("fight").getWorldFolder().delete();


                        redsec = 72000;


                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                redsec -= 1;
                                if (redsec == 0) {
                                    cooltime.put(getServer().getPlayer(pp1).getUniqueId(), true);
                                    cooltime.put(getServer().getPlayer(pp2).getUniqueId(), true);

                                    cancel();
                                }


                            }
                        }.runTaskTimer(getPlugin(Pvp.class), 0L, 1L);

                        cancel();
                    }


                }
            }.runTaskTimer(this, 0L, 20L);


        }
        else if (e.getEntity().getName().equalsIgnoreCase(p2) && isgame&&!isready) {
            String temp = p1;
            p1 = p2;
            p2 = temp;
            Location temp2 = l1;
            l1 = l2;
            l2 = temp2;
            isready = true;
            consol.sendMessage(p2 + "님이 승리하였습니다.");
            getServer().getPlayer(p1).getEnderChest().clear();
            cooltime.put(getServer().getPlayer(p1).getUniqueId(), false);
            cooltime.put(getServer().getPlayer(p2).getUniqueId(), false);
            getServer().getPlayer(p2).sendMessage(ChatColor.AQUA + "승리하셨습니다.");
            getServer().getPlayer(p2).sendMessage(ChatColor.YELLOW + "5분 뒤 자동으로 마지막 장소로 이동합니다.");
            redsec = 300;
            BossBar bossBar = Bukkit.createBossBar("템 정리시간", BarColor.BLUE, BarStyle.SOLID);
            bossBar.addPlayer(getServer().getPlayer(p2));
            e.getEntity().getPlayer().setGlowing(false);
            getServer().getPlayer(p2).setGlowing(false);
            bossBar.setProgress(((float) redsec / 300));
            bossBar.setVisible(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    redsec -= 1;

                    bossBar.setProgress(((float) redsec / 300));
                    if (redsec == 0) {
                        isgame = false;
                        isready = false;
                        bossBar.removePlayer(getServer().getPlayer(p2));
                        bossBar.setVisible(false);

                        getServer().getPlayer(p2).teleport(l2);
                        String pp1 = p1;
                        String pp2 = p2;
                        p1 = null;
                        p2 = null;
                        l1 = null;
                        l2 = null;

                        getServer().getWorld("fight").getWorldFolder().delete();


                        redsec = 72000;


                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                redsec -= 1;
                                if (redsec == 0) {
                                    cooltime.put(getServer().getPlayer(pp1).getUniqueId(), true);
                                    cooltime.put(getServer().getPlayer(pp2).getUniqueId(), true);

                                    cancel();
                                }


                            }
                        }.runTaskTimer(getPlugin(Pvp.class), 0L, 1L);

                        cancel();
                    }


                }
            }.runTaskTimer(this, 0L, 20L);

        }


    }


    public void Worldcreater ()
    {
        World world;
        WorldCreator seed = new WorldCreator("fight");
        seed.environment(World.Environment.NORMAL);
        world = seed.createWorld();




    }



}

