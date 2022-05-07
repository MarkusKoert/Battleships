package Packets;

public class PacketUpdatePlayerInfo {
    private float x, y, angle, shootDelay, maxSpeed;
    private int currentHealth, id, maxHealth, bulletDamage, bulletSpeedMultiplier;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int health) {
        this.currentHealth = health;
    }

    public float getShootDelay() {
        return shootDelay;
    }

    public void setShootDelay(float shootDelay) {
        this.shootDelay = shootDelay;
    }

    public int getBulletDamage() {
        return bulletDamage;
    }

    public void setBulletDamage(int bulletDamage) {
        this.bulletDamage = bulletDamage;
    }

    public int getBulletSpeedMultiplier() {
        return bulletSpeedMultiplier;
    }

    public void setBulletSpeedMultiplier(int bulletSpeedMultiplier) {
        this.bulletSpeedMultiplier = bulletSpeedMultiplier;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
}
