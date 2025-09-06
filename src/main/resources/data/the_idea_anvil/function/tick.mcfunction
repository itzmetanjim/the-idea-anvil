execute as @a[scores={idea_anvil_mana=..99}] run scoreboard players add @s idea_anvil_mana 1
# █ has width 8 pixels (represents 10 mana), ▌ has 4 pixels and space also has 4 pixels (represents 5 mana)
#  #60EFFF #5BF0F9 #56F1F3 #52F1ED #4DF2E7 #48F3E1 #43F4DB #3EF5D5 #3AF5CF #35F6C9 #30F7C3 #2BF8BD #26F9B7 #22F9B1 #1DFAAB #18FBA5 #13FC9F #0EFD99 #0AFD93 #05FE8D #00FF87
execute as @a[scores={idea_anvil_mana=0..4}] run tellraw @s {"text":"Mana: [          ]","color":"#60EFFF"}
execute as @a[scores={idea_anvil_mana=5..9}] run tellraw @s {"text":"Mana: [▌         ]","color":"#5BF0F9"}
execute as @a[scores={idea_anvil_mana=10..14}] run tellraw @s {"text":"Mana: [█         ]","color":"#56F1F3"}
execute as @a[scores={idea_anvil_mana=15..19}] run tellraw @s {"text":"Mana: [█▌        ]","color":"#52F1ED"}
execute as @a[scores={idea_anvil_mana=20..24}] run tellraw @s {"text":"Mana: [██        ]","color":"#4DF2E7"}
execute as @a[scores={idea_anvil_mana=25..29}] run tellraw @s {"text":"Mana: [██▌       ]","color":"#48F3E1"}
execute as @a[scores={idea_anvil_mana=30..34}] run tellraw @s {"text":"Mana: [███       ]","color":"#43F4DB"}
execute as @a[scores={idea_anvil_mana=35..39}] run tellraw @s {"text":"Mana: [███▌      ]","color":"#3EF5D5"}
execute as @a[scores={idea_anvil_mana=40..44}] run tellraw @s {"text":"Mana: [████      ]","color":"#3AF5CF"}
execute as @a[scores={idea_anvil_mana=45..49}] run tellraw @s {"text":"Mana: [████▌     ]","color":"#35F6C9"}
execute as @a[scores={idea_anvil_mana=50..54}] run tellraw @s {"text":"Mana: [█████     ]","color":"#30F7C3"}
execute as @a[scores={idea_anvil_mana=55..59}] run tellraw @s {"text":"Mana: [█████▌    ]","color":"#2BF8BD"}
execute as @a[scores={idea_anvil_mana=60..64}] run tellraw @s {"text":"Mana: [██████    ]","color":"#26F9B7"}
execute as @a[scores={idea_anvil_mana=65..69}] run tellraw @s {"text":"Mana: [██████▌   ]","color":"#22F9B1"}
execute as @a[scores={idea_anvil_mana=70..74}] run tellraw @s {"text":"Mana: [███████   ]","color":"#1DFAAB"}
execute as @a[scores={idea_anvil_mana=75..79}] run tellraw @s {"text":"Mana: [███████▌  ]","color":"#18FBA5"}
execute as @a[scores={idea_anvil_mana=80..84}] run tellraw @s {"text":"Mana: [████████  ]","color":"#13FC9F"}
execute as @a[scores={idea_anvil_mana=85..89}] run tellraw @s {"text":"Mana: [████████▌ ]","color":"#0EFD99"}
execute as @a[scores={idea_anvil_mana=90..94}] run tellraw @s {"text":"Mana: [█████████ ]","color":"#0AFD93"}
execute as @a[scores={idea_anvil_mana=95..98}] run tellraw @s {"text":"Mana: [█████████▌]","color":"#05FE8D"}
execute as @a[scores={idea_anvil_mana=99}] run tellraw @s {"text":"Mana: [██████████]","color":"#00FFEE","bold":true}
