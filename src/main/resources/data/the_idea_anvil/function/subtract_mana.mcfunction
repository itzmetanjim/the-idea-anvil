execute as @s run scoreboard players set @s idea_anvil_subt_success 0
$execute as @s[scores={idea_anvil_mana=$(key_1)..}] run scoreboard players set @s idea_anvil_subt_success 1
$execute as @s[scores={idea_anvil_mana=$(key_1)..}] run scoreboard players remove @s idea_anvil_mana $(key_1)
