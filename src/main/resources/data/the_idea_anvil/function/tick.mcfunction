execute as @a[tag=!has_mana_init] run scoreboard players set @s idea_anvil_mana 0
execute as @a[tag=!has_mana_init] run tag @s add has_mana_init
execute as @a[scores={idea_anvil_mana=..1000}] run scoreboard players add @s idea_anvil_mana 2
# █ has width 8 pixels (represents 10 mana), ▌ has 4 pixels and space also has 4 pixels (represents 5 mana)
#  #60EFFF #5BF0F9 #56F1F3 #52F1ED #4DF2E7 #48F3E1 #43F4DB #3EF5D5 #3AF5CF #35F6C9 #30F7C3 #2BF8BD #26F9B7 #22F9B1 #1DFAAB #18FBA5 #13FC9F #0EFD99 #0AFD93 #05FE8D #00FF87
execute as @a[scores={idea_anvil_mana=0..49}] run title @s actionbar {"text":"Mana: [          ]","color":"#60EFFF"}
execute as @a[scores={idea_anvil_mana=41..99}] run title @s actionbar {"text":"Mana: [▌         ]","color":"#5BF0F9"}
execute as @a[scores={idea_anvil_mana=100..149}] run title @s actionbar {"text":"Mana: [█         ]","color":"#56F1F3"}
execute as @a[scores={idea_anvil_mana=150..199}] run title @s actionbar {"text":"Mana: [█▌        ]","color":"#52F1ED"}
execute as @a[scores={idea_anvil_mana=200..249}] run title @s actionbar {"text":"Mana: [██        ]","color":"#4DF2E7"}
execute as @a[scores={idea_anvil_mana=250..299}] run title @s actionbar {"text":"Mana: [██▌       ]","color":"#48F3E1"}
execute as @a[scores={idea_anvil_mana=300..349}] run title @s actionbar {"text":"Mana: [███       ]","color":"#43F4DB"}
execute as @a[scores={idea_anvil_mana=350..399}] run title @s actionbar {"text":"Mana: [███▌      ]","color":"#3EF5D5"}
execute as @a[scores={idea_anvil_mana=400..449}] run title @s actionbar {"text":"Mana: [████      ]","color":"#3AF5CF"}
execute as @a[scores={idea_anvil_mana=450..499}] run title @s actionbar {"text":"Mana: [████▌     ]","color":"#35F6C9"}
execute as @a[scores={idea_anvil_mana=500..549}] run title @s actionbar {"text":"Mana: [█████     ]","color":"#30F7C3"}
execute as @a[scores={idea_anvil_mana=550..599}] run title @s actionbar {"text":"Mana: [█████▌    ]","color":"#2BF8BD"}
execute as @a[scores={idea_anvil_mana=600..649}] run title @s actionbar {"text":"Mana: [██████    ]","color":"#26F9B7"}
execute as @a[scores={idea_anvil_mana=650..699}] run title @s actionbar {"text":"Mana: [██████▌   ]","color":"#22F9B1"}
execute as @a[scores={idea_anvil_mana=700..749}] run title @s actionbar {"text":"Mana: [███████   ]","color":"#1DFAAB"}
execute as @a[scores={idea_anvil_mana=750..799}] run title @s actionbar {"text":"Mana: [███████▌  ]","color":"#18FBA5"}
execute as @a[scores={idea_anvil_mana=800..849}] run title @s actionbar {"text":"Mana: [████████  ]","color":"#13FC9F"}
execute as @a[scores={idea_anvil_mana=850..899}] run title @s actionbar {"text":"Mana: [████████▌ ]","color":"#0EFD99"}
execute as @a[scores={idea_anvil_mana=900..949}] run title @s actionbar {"text":"Mana: [█████████ ]","color":"#0AFD93"}
execute as @a[scores={idea_anvil_mana=950..998}] run title @s actionbar {"text":"Mana: [█████████▌]","color":"#05FE8D"}
execute as @a[scores={idea_anvil_mana=999}] run title @s actionbar {"text":"Mana: [██████████]","color":"#00FFEE","bold":true}