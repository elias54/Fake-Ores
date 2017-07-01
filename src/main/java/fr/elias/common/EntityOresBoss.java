package fr.elias.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;

public class EntityOresBoss extends EntityMob {

	public int phase;
    private final BossInfoServer bossInfo = (BossInfoServer)(new BossInfoServer(this.getDisplayName(), BossInfo.Color.BLUE, BossInfo.Overlay.PROGRESS)).setDarkenSky(false);
	
	public EntityOresBoss(World worldIn)
	{
		super(worldIn);
		phase = 1;
        this.isImmuneToFire = true;
        this.experienceValue = rand.nextInt(5000);
        setSize(2.7F, 4.6F); 
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(4, new EntityOresBoss.AIAttackPhase());
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.5D));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(800D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(18D);
    }
    public void updateAITasks()
    {
    	super.updateAITasks();
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    }
    public void onLivingUpdate()
    {
    	if(phase == 1)
    	{
    		if(this.getHealth() < 500)
    		{
    			spawnPhaseChangeParticle();
    			world.playSound(posX, posY, posZ, FakeOresSoundEvent.oreboss_phase2, SoundCategory.HOSTILE, 100F, 1.0F, false);
    			phase = 2;
    		}
    	}
    	if(phase == 2)
    	{
    		if(this.getHealth() < 200)
    		{
    			spawnPhaseChangeParticle();
    			world.playSound(posX, posY, posZ, FakeOresSoundEvent.oreboss_phase3, SoundCategory.HOSTILE, 100F, 1.0F, false);
    			phase = 3;
    		}
    		teleport(1520);
    	}
    	if(phase == 3)
    	{
    		teleport(775);
    	}
    	super.onLivingUpdate();
    }
    public void spawnPhaseChangeParticle()
    {
    	for (int j = 0; j < phase; j++)
    	{
    		this.spawnExplosionParticle();
    		world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (posX + rand.nextDouble() * phase) - 0.5D * phase, posY + rand.nextDouble() * phase, (posZ + rand.nextDouble() * phase) - 0.5D * phase, 0.0D, 0.0D, 0.0D, new int[0]);
    	}
    }
    public void attackEntityWithRangedAttack(EntityLivingBase par1EntityLivingBase, float par2)
    {
        EntitySnowball entitysnowball = new EntitySnowball(this.world, this);
        EntityArrow entityarrow = new EntityTippedArrow(this.world, this);
        double d0 = par1EntityLivingBase.posX - this.posX;
        double d1 = par1EntityLivingBase.posY + (double)par1EntityLivingBase.getEyeHeight() - 1.100000023841858D - entitysnowball.posY;
        double d2 = par1EntityLivingBase.posZ - this.posZ;
        float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;
        entitysnowball.setThrowableHeading(d0, d1 + (double)f1, d2, 1.6F, 12.0F);
        entityarrow.setThrowableHeading(d0, d1 + (double)f1, d2, 1.6F, 12.0F);
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.spawnEntity(entityarrow);
        this.world.spawnEntity(entitysnowball);
    }
    
    public void attackEntity(Entity entity, float f)
    {
    	if(phase == 1)
    	{
            double d0 = entity.posX - this.posX;
            double d1 = entity.getEntityBoundingBox().minY + (double)(entity.height / 2.0F) - (this.posY + (double)(this.height / 2.0F));
            double d2 = entity.posZ - this.posZ;
            if(rand.nextInt(34) == 0)
            {
                float f1 = MathHelper.sqrt(f) * 0.5F;
                this.world.playEvent((EntityPlayer)null, 1009, new BlockPos((int)this.posX, (int)this.posY, (int)this.posZ), 0);

                for (int i = 0; i < 1; ++i)
                {
                    EntityLargeFireball entitysmallfireball = new EntityLargeFireball(this.world, this, d0 + this.rand.nextGaussian() * (double)f1, d1, d2 + this.rand.nextGaussian() * (double)f1);
                    entitysmallfireball.posY = this.posY + (double)(this.height / 2.0F) + 0.5D;
                    this.world.spawnEntity(entitysmallfireball);
                }
            }
    	}
    	if(phase == 2)
    	{
    		if(rand.nextInt(50) == 0)
    		{
        		attackEntityWithRangedAttack((EntityLivingBase)entity, f);
    		}
    		if(rand.nextInt(220) == 0)
    		{
    			this.spawnSomeSbires("all", posX + rand.nextInt(5), posY + 3, posZ + rand.nextInt(5));
    		}
    	}
    	if(phase == 3)
    	{
    		if(rand.nextInt(100) == 0)
    		{
        		world.addWeatherEffect(new EntityLightningBolt(world, rand.nextInt(2) == 0 ? posX : entity.posX, rand.nextInt(2) == 0 ? posY : entity.posY, rand.nextInt(2) == 0 ? posZ : entity.posZ, false));
    		}
    		if(rand.nextInt(8) == 0)
    		{
        		attackEntityWithRangedAttack((EntityLivingBase)entity, f);
    		}
    	}
    }
    
    public class AIAttackPhase extends EntityAIBase {

    	public EntityOresBoss boss = EntityOresBoss.this;
    	public int attackTime;
    	public AIAttackPhase() {}
    	
		@Override
		public boolean shouldExecute() {
            EntityLivingBase entitylivingbase = this.boss.getAttackTarget();
            return entitylivingbase != null && entitylivingbase.isEntityAlive();
		}
        public void updateTask()
        {
        	--attackTime;
            EntityLivingBase entitylivingbase = this.boss.getAttackTarget();
            double d0 = this.boss.getDistanceSqToEntity(entitylivingbase);

            if (d0 < 4.0D)
            {
                if (this.attackTime <= 0)
                {
                    this.attackTime = 20;
                    this.boss.attackEntityAsMob(entitylivingbase);
                }
                
                this.boss.getMoveHelper().setMoveTo(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, 1.0D);
            }
            else if (d0 < 256.0D)
            {
                // ATTACK ENTITY GOES HERE
                boss.attackEntity(entitylivingbase, (float)d0);
                this.boss.getLookHelper().setLookPositionWithEntity(entitylivingbase, 10.0F, 10.0F);
            }
            else
            {
                this.boss.getNavigator().clearPathEntity();
                this.boss.getMoveHelper().setMoveTo(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, 0.5D);
            }
        }
    }
    public void spawnSomeSbires(String entityToSpawn, double x, double y, double z)
    {
		EntityDiamondOre diamondOre = new EntityDiamondOre(world);
		EntityEmeraldOre emeraldOre = new EntityEmeraldOre(world);
		EntityGoldOre goldOre = new EntityGoldOre(world);
		EntityIronOre ironOre = new EntityIronOre(world);
		EntityCoalOre coalOre = new EntityCoalOre(world);
		EntityRedstoneOre redstoneOre = new EntityRedstoneOre(world);
		EntityNetherQuartzOre quartzOre = new EntityNetherQuartzOre(world);
		EntityLapisOre lapisOre = new EntityLapisOre(world);
		
		diamondOre.setPosition(x, y, z);
		emeraldOre.setPosition(x, y, z);
		goldOre.setPosition(x, y, z);
		ironOre.setPosition(x, y, z);
		coalOre.setPosition(x, y, z);
		redstoneOre.setPosition(x, y, z);
		quartzOre.setPosition(x, y, z);
		lapisOre.setPosition(x, y, z);
    	if(entityToSpawn.equals("diamond"))
    	{
    		world.spawnEntity(diamondOre);
    	}
    	if(entityToSpawn.equals("emerald"))
    	{
    		world.spawnEntity(emeraldOre);
    	}
    	if(entityToSpawn.equals("gold"))
    	{
    		world.spawnEntity(goldOre);
    	}
    	if(entityToSpawn.equals("iron"))
    	{
    		world.spawnEntity(ironOre);
    	}
    	if(entityToSpawn.equals("coal"))
    	{
    		world.spawnEntity(coalOre);
    	}
    	if(entityToSpawn.equals("redstone"))
    	{
    		world.spawnEntity(redstoneOre);
    	}
    	if(entityToSpawn.equals("quartz"))
    	{
    		world.spawnEntity(quartzOre);
    	}
    	if(entityToSpawn.equals("lapis"))
    	{
    		world.spawnEntity(lapisOre);
    	}
    	if(entityToSpawn.equals("all"))
    	{
    		world.spawnEntity(diamondOre);
    		world.spawnEntity(emeraldOre);
    		world.spawnEntity(goldOre);
    		world.spawnEntity(ironOre);
    		world.spawnEntity(coalOre);
    		world.spawnEntity(redstoneOre);
    		world.spawnEntity(quartzOre);
    		world.spawnEntity(lapisOre);
    	}
    }
    
    public void teleport(int cooldownBeforeTeleport)
    {
		if(rand.nextInt(cooldownBeforeTeleport) == 0)
		{
			EntityBossTeleporter teleporter = new EntityBossTeleporter(world, this);
			//teleporter.func_184538_a(this, this.rotationPitch, this.rotationYaw, 0.0F, 1.5F, 1.0F);
			//TODO
			teleporter.setHeadingFromThrower(this, this.rotationPitch, this.rotationYaw, 0.0F, 1.5F, 1.0F);
			world.spawnEntity(teleporter);
		}
    }
    
    public boolean attackEntityFrom(DamageSource damagesource, float f)
    {
    	Entity entity = damagesource.getTrueSource();
    	if(entity != null && entity instanceof EntityPlayer)
    	{
    		ItemStack itemstack = ((EntityPlayer)entity).inventory.getCurrentItem();
    		if(itemstack != null)
    		{
    			if(itemstack.getItem() == FakeOres.antiOresBlade)
    			{
    				f = 35 + rand.nextFloat();
    			}
    		}
        	if(entity instanceof EntityLightningBolt)
        	{
        		f = 0f;
        	}
        	
        	if(entity instanceof EntityLargeFireball)
        	{
        		f = 75f;
        	}
        	if(this.phase == 2)
        	{
        		f = 50f;
        	}
    	}
    	return super.attackEntityFrom(damagesource, f);
    }
    public void onDeath(DamageSource sourceOfDamage)
    {
    	EntityCup cup = new EntityCup(world);
    	cup.setPosition(posX, posY + 0.5D, posZ);
    	if(!world.isRemote)
    	{
    		world.spawnEntity(cup);
    		this.dropItem(FakeOres.red_gem, 1);
    	}
		//((EntityPlayer)sourceOfDamage.getEntity()).addStat(FakeOres.boss_defeated);
		if(rand.nextInt(100) == 0)
		{
			if(!world.isRemote)
			{
				this.dropItemWithOffset(FakeOres.antiOresBlade, 1, (float) (this.posY + 1));
			}
		}
		super.onDeath(sourceOfDamage);
    }
    public int getPhase()
    {
    	return phase;
    }
    /**
     * Add the given player to the list of players tracking this entity. For instance, a player may track a boss in
     * order to view its associated boss bar.
     */
    public void addTrackingPlayer(EntityPlayerMP player)
    {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    /**
     * Removes the given player from the list of players tracking this entity. See {@link Entity#addTrackingPlayer} for
     * more information on tracking.
     */
    public void removeTrackingPlayer(EntityPlayerMP player)
    {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }
    public boolean isNonBoss()
    {
        return false;
    }
    public void onDeathUpdate()
    {
    	spawnPhaseChangeParticle();
    	super.onDeathUpdate();
    }
    
    protected SoundEvent getHurtSound(DamageSource p_184601_1_)
    {
        return FakeOresSoundEvent.oreboss_hurt;
    }

    protected SoundEvent getDeathSound()
    {
        return FakeOresSoundEvent.oreboss_death;
    }
}
