package me.jadenp.notskills;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Spells {
    public static void absorb(Player p){
        Random rand = new Random();

        Particle.DustOptions dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(0, 0, 0), 10);
        Location location = p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(3));
        int slot = p.getInventory().getHeldItemSlot();

        new BukkitRunnable(){
            int timer = 0;
            @Override
            public void run() {
                p.getInventory().setHeldItemSlot(slot);
                if (p.isOnline()){
                    for (int i = 0; i < 10; i++)
                        p.spawnParticle(Particle.REDSTONE, new Location(location.getWorld(),location.getX() + (((double)rand.nextInt(8) / 10)-0.4),location.getY() + (((double)rand.nextInt(8) / 10)-0.4),location.getZ() + (((double)rand.nextInt(8) / 10)-0.4)), 1, dustOptions);
                    Location particle = new Location(location.getWorld(),location.getX() + (((double)rand.nextInt(20))-10),location.getY() + (((double)rand.nextInt(20))-10),location.getZ() + (((double)rand.nextInt(20))-10));
                    Vector vector = location.toVector().subtract(particle.toVector()).normalize();
                    p.spawnParticle(Particle.SQUID_INK, particle , 0, vector.getX(),vector.getY(),vector.getZ());
                    p.spawnParticle(Particle.CRIT_MAGIC,p.getLocation(),15,1,1,1);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,15,20));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 15, 250));
                    double radius = 10D;
                    List<Entity> near = Objects.requireNonNull(p.getWorld()).getEntities();
                    for (Entity e: near) {
                        if (e.getLocation().distance(location) <= radius) {
                            if (e instanceof LivingEntity) {
                                if (e != p) {

                                    if (e.getLocation().distance(location) <= 2) {
                                        e.setVelocity(new Vector(0,0,0));
                                        ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,15,1));
                                        if (((LivingEntity) e).getHealth() < 30 && !(e instanceof Player)){
                                            p.playSound(e.getLocation(), Sound.ENTITY_STRIDER_EAT,1,1);
                                            p.spawnParticle(Particle.SUSPENDED,e.getLocation(),20,1,1,1);
                                            e.remove();
                                        } else {
                                            ((LivingEntity) e).damage(30,p);
                                        }

                                    } else {
                                        Vector v = location.toVector().subtract(e.getLocation().toVector()).normalize();
                                        e.setVelocity(e.getVelocity().add(v.multiply((10 - e.getLocation().distance(p.getLocation())) / 5)));
                                    }
                                }

                            }
                        }
                    }
                    if (timer > 8){
                        this.cancel();
                    }
                    timer++;
                } else {
                    this.cancel();
                }

            }
        }.runTaskTimer(NotSkills.getInstance(),0,10L);

    }

    public static void iceShards(Player p) {

        Particle.DustOptions dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(10, 50, 200), 1);
        Location front = p.getEyeLocation().add(p.getLocation().getDirection().multiply(1.3));
        p.getWorld().spawnParticle(Particle.REDSTONE, front, 1, dustOptions);
        Vector d = p.getLocation().getDirection();
        new BukkitRunnable() {
            int timer = 0;

            @Override
            public void run() {
                Location loc = front.add(d.multiply(1 + (timer / 10)));
                p.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, dustOptions);
                timer++;
                if (loc.getBlock().getType() != Material.AIR) {
                    this.cancel();
                    return;
                }
                loc = front.add(d.multiply(1 + (timer / 10)));
                p.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, dustOptions);
                timer++;
                double radius = 2D;
                List<Entity> near = Objects.requireNonNull(loc.getWorld()).getEntities();
                for (Entity e : near) {
                    if (e.getLocation().distance(loc) <= radius) {
                        if (e instanceof LivingEntity) {
                            if (e != p) {

                                ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW,60,1));
                                ((LivingEntity) e).damage(20, p);

                            }
                        }
                    }
                }
                if (loc.getBlock().getType() != Material.AIR) {
                    this.cancel();
                }
                if (timer == 16) {
                    this.cancel();
                }
            }
        }.runTaskTimer(NotSkills.getInstance(), 0, 1L);
    }

    public static void snipe(Player p) {

        Location launchLocation = p.getEyeLocation().add(p.getEyeLocation().getDirection());
        Vector launchDirection = p.getEyeLocation().getDirection().normalize();
        assert launchLocation.getWorld() != null;
        // 8 places the particles can come from
        Vector down = new Vector(0,-1,0);
        Vector up = new Vector(0,1,0);
        Vector side1 = launchDirection.getCrossProduct(down); // pretend this is right
        Vector side2 = new Vector(0 - side1.getX(), 0 - side1.getY(), 0 - side1.getZ()); // pretend this is left
        // these are the between vectors (I think of them as quartiles because they are in the middle if you graph the origin as launchLocation)
        Vector q1 = new Vector(side1.getX() + up.getX(), side1.getY() + up.getY(), side1.getZ() + up.getZ()).normalize();
        Vector q2 = new Vector(side2.getX() + up.getX(), side2.getY() + up.getY(), side2.getZ() + up.getZ()).normalize();
        Vector q3 = new Vector(side2.getX() + down.getX(), side2.getY() + down.getY(), side2.getZ() + down.getZ()).normalize();
        Vector q4 = new Vector(side1.getX() + down.getX(), side1.getY() + down.getY(), side1.getZ() + down.getZ()).normalize();
        // adding them to a list, so I can select one of them randomly with a random number gen
        List<Vector> particlePlaces = new ArrayList<>();
        particlePlaces.add(down);
        particlePlaces.add(up);
        particlePlaces.add(side1);
        particlePlaces.add(side2);
        particlePlaces.add(q1);
        particlePlaces.add(q2);
        particlePlaces.add(q3);
        particlePlaces.add(q4);

        // particle distance from launch
        float PDFL = 1.0f;
        float particleSpeed = 1.0f;
        // how many particles will spawn everytime the runnable goes through
        int particlesPerRun = 3; // total particles = particlesPerRun * 6

        // spawn particles
        new BukkitRunnable(){
            int runs = 0;
            @Override
            public void run() {
                if (runs < 60){
                    // some kewl particles
                    for (int i = 0; i < particlesPerRun; i++) {
                        Vector randomOf8 = particlePlaces.get((int) (Math.random() * 8));
                        //                                                                  adding the vector to the launch location to get the starting point of the vector - PDFL so I can adjust how far away it starts                               getting the reverse vector so it can shoot in the opposite direction back into launchLocation, particleSpeed so I can adjust how fast it comes back & so it doesnt overshoot
                        launchLocation.getWorld().spawnParticle(Particle.REVERSE_PORTAL, launchLocation.getX() + (randomOf8.getX() * PDFL), launchLocation.getY() + (randomOf8.getY() * PDFL), launchLocation.getZ() + (randomOf8.getZ() * PDFL), 1, 0 - (randomOf8.getX() * particleSpeed), 0 - (randomOf8.getY() * particleSpeed), 0 - (randomOf8.getZ() * particleSpeed));
                    }
                } else {
                    this.cancel();
                    // launch the arrow
                    Arrow arrow = launchLocation.getWorld().spawnArrow(launchLocation, launchDirection, 2f, 6);
                    arrow.setShooter(p);
                    arrow.setDamage(10);
                }
                runs++;
            }
        }.runTaskTimer(NotSkills.getInstance(),0L,1L);


        //arrow.setMetadata("magic", new FixedMetadataValue(plugin, true));
    }
}
