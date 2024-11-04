package ch.mc.jr.labyrinth;

import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class Command implements CommandExecutor {

    Random random = new Random();
    private static final int HEIGHT = 3;
    Map<XZCords,Location> locationMap = new HashMap<>();

    List<Vector> allVectors = new ArrayList<>();

    public Command() {
        allVectors.add(new Vector(1,0,0));
        allVectors.add(new Vector(-1,0,0));
        allVectors.add(new Vector(0,0,-1));
        allVectors.add(new Vector(0,0,1));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)){
           sender.sendMessage("Befehl nur für spieler");
           return true;
        }

        Player player = (Player) sender;
        if(command.getName().equalsIgnoreCase("maze")){
            int length;
            String version = "v1";

            if (args.length > 1) {
                version = args[1];
                if (!version.equalsIgnoreCase("v1") && !version.equalsIgnoreCase("v2")) {
                    player.sendMessage("v1 oder v2 angeben");
                    return false;
                }
            }
            if (args.length == 0) {
                length = 30;
            }else{
                try {
                    length = Integer.parseInt(args[0]);
                    if(length < 4){
                        player.sendMessage("zu klein");
                        return false;
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage("Ungültiger Parameter. Bitte gib eine gültige Zahl an.");
                    return false;
                }
            }


            block(player.getLocation(), Material.STONE, length);
            path(player.getLocation(),length, version );
            player.teleport(middle(player.getLocation(),length));

            return true;
        }
        return false;
    }
    private Location middle(Location location, int length){
        Vector direction = location.getDirection();
        Location l = location.clone();
        if(direction.getX() < 0){
            l.add(-length/2.0,0,0);
        }else{
            l.add(length/2.0,0,0);
        }
        if(direction.getZ() < 0){
            l.add(0,0,-length/2.0);
        }else{
            l.add(0,0,length/2.0);
        }
        return l;
    }
    private void block(Location location, Material material, int length){
        XZCords direction = getBuildDirection(location);
        for (int i =0; i<length;i++){
            Location l = location.clone();
            for(int a = 0; a < length; a++){
                setBlockType(l,material);
                l.add(0,0,direction.getZ());
            }
            location.add(direction.getX(),0,0);
        }
    }

    private void path(Location start, int length, String version){
        Location loc = middle(start, length);
        XZCords direction = getBuildDirection(start);
        locationMap.put(new XZCords(loc),loc);
        dig(loc);
        List<Location> alloptions = new ArrayList<>();
        while (!locationMap.isEmpty()){
            for(Vector vec : allVectors){
                Location candidate = loc.clone().add(vec);
                if (version.equalsIgnoreCase("v2")){
                    Bukkit.broadcastMessage("v2");
                    if(!isAir(candidate)){
                        Location forward = candidate.clone().add(vec);
                        Location right = candidate.clone().add(rotate90Degrees(vec));
                        Location left = candidate.clone().add(rotate270Degrees(vec));
                        Location forwardLeft = forward.clone().add(rotate270Degrees(vec));
                        Location forwardRight = forward.clone().add(rotate90Degrees(vec));
                        if(!isAir(forward) && !isAir(right) && !isAir(left) && !isAir(forwardLeft) && !isAir(forwardRight)){
                            alloptions.add(candidate);
                        }
                    }
                }else{
                    if(!isAir(candidate)){
                        Location forward = candidate.clone().add(vec);
                        Location right = candidate.clone().add(rotate90Degrees(vec));
                        Location left = candidate.clone().add(rotate270Degrees(vec));
                        if(!isAir(forward) && !isAir(right) && !isAir(left)){
                            alloptions.add(candidate);
                        }
                    }
                }
            }
            if (alloptions.isEmpty()){
                locationMap.remove(new XZCords(loc));
                if (!locationMap.isEmpty()){
                    List<Location> remainingLocations = new ArrayList<>( locationMap.values());
                    loc = randomLocationFrom(remainingLocations);
                }
            }else {
                loc = randomLocationFrom(alloptions);
                dig(loc);
                locationMap.put(new XZCords(loc),loc);
                alloptions.clear();
            }
        }
        start.add(direction.getX(),0,0);
        while(!isAir(start)){
            dig(start);
            start.add(0,0,direction.getZ());
        }
    }
    private XZCords getBuildDirection(Location loc) {
        Vector direction = loc.getDirection();
        int dx;
        int dz;
        if(direction.getX() < 0){
            dx = -1;
        }else{
            dx = 1;
        }
        if(direction.getZ() < 0){
            dz = -1;
        }else{
            dz = 1;
        }
        return new XZCords(dx,dz);
    }
    private void setBlockType(Location loc, Material material){
        int y = loc.getBlockY();
        for(int h=0;h<HEIGHT;h++){
            loc.getBlock().setType(material);
            loc.add(0,1,0);
        }
        loc.setY(y);
    }
    private void dig(Location location){
        setBlockType(location,Material.AIR);
    }
    private boolean isAir(Location loc){
        return loc.getBlock().getType() == Material.AIR;
    }

    private Location randomLocationFrom(List<Location> list){
        return list.get(random.nextInt(list.size()));
    }

    private Vector rotate270Degrees(Vector v) {
        return new Vector(v.getZ(), v.getY(), -v.getX());
    }

    private Vector rotate90Degrees(Vector v) {
        return new Vector(-v.getZ(), v.getY(), v.getX());
    }
}