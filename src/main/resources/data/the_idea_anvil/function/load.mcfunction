#Runs when server loads
scoreboard objectives add idea_anvil_mana dummy "Mana"
scoreboard objectives add idea_anvil_cache dummy "Cache"
scoreboard objectives add idea_anvil_subt_success dummy "If subtraction was successfull"
tellraw @a {"text":"[The Idea Anvil] Mod loaded successfully!","color":"green"}