![The idea anvil logo](https://cdn.modrinth.com/data/cached_images/fffaddc7d41fa463eff357cacfeca75fc69b46b8.png)

<div align="center" style="display: flex; gap: 16px; justify-content: center;">
  <a href="https://github.com/itzmetanjim/the-idea-anvil">
    <img src="https://cdn.modrinth.com/data/cached_images/14bb5f6380dbf0e9a0bc20179ef4d9728b0f88d9.png" alt="Github icon" height="60">
  </a>
  <a href="https://modrinth.com/mod/idea_anvil">
    <img src="https://cdn.modrinth.com/data/cached_images/2df5ae65196aa7a4a0aef20e208c0005ff06471f.png" alt="Modrinth icon" height="60">
  </a>
</div>

![Bell Icon with heading for About](https://cdn.modrinth.com/data/cached_images/9872283263bf494fa11d3c3b54326d1b5f64c0ba_0.webp)

This mod allows you to get **literally any item** with the help of AI. **This is mainly for singleplayer/multiplayer with friends, as people can ask the AI for OP items.** Prompting the AI to give balanced items and adding a price system could work, but adversarial prompts could still trick the AI so instead of lulling server owners into a false sense of security, I decided to make it possible to get OP items as well. You can easily make a few items then convert them to a datapack manually. I will add automatic datapack conversion later.

![Diamond icon with heading for Features](https://cdn.modrinth.com/data/cached_images/0a158a66eb2a9b28d3b096ca6b863247680bfc3b_0.webp)

**Use the `/idea` or `/ai` command to make items...**

- `/idea an item that gives speed temporarily`
- `/idea a lightning sword`
- `/idea OPERATOR a shard which gives diamonds`
- `/idea OPERATOR an item giving me creative mode` 

(OPERATOR makes sure the AI does not reject the request)

**...or make items yourself, without the `idea`/`ai` command!**
Get a green amethyst shard which gives diamonds on right click.

`/give @s the_idea_anvil:custom_item[minecraft:item_model= "the_idea_anvil:amethyst_shard_lime", the_idea_anvil:use_cmd="give @s diamond", minecraft:item_name="Diamond giving shard"]`

Check the Github wiki page for more details.

**A global mana system for all items is available to help in making cooldowns.**

The AI will use this feature for most of its items. You can also use it in your own items:

`/give @s the_idea_anvil:custom_item[minecraft:item_model= "the_idea_anvil:amethyst_shard_lime", the_idea_anvil:use_cmd="function the_idea_anvil:subtract_mana {key_1:\"100\"} #/ execute as @s[scores={idea_anvil_subt_success=1}] give @s diamond", minecraft:item_name="Diamond giving shard"]`

![Painting icon with heading for Screenshots / Media](https://cdn.modrinth.com/data/cached_images/0e39ebe56a7cbf7f8cd6472257b22340e0b10e33_0.webp)

![Screenshot](https://cdn.modrinth.com/data/cached_images/539795e6d01da36e62910c9f1b628632f0b1fd27.png)

![Screenshot](https://cdn.modrinth.com/data/cached_images/4683d529b6d2234b366438dc57dfe43bd810dbae.png)

![Screenshot](https://cdn.modrinth.com/data/cached_images/261c34e08eded95796653ca726c8b229d0fbe74c.png)

![Barrier icon with heading for Known Issues](https://cdn.modrinth.com/data/cached_images/6e5b2599c9c8720694bdaa7e930547bff808d486.png)

- First of all, **since this mod lets you get OP items, it _can not_ be considered survival friendly.** To slightly mitigate this, the commands are still limited to permission level 2, which includes `/give` `/summon` `/gamemode` `/execute` (which does not change permission level),etc, but **does not include commands like** `/op` `/deop` `/ban` `/kick` `/stop` etc.

- The AI can be slightly buggy, occasionally not giving items or giving items with invalid textures.

- If a player changes username, mana may not work because tags carry over to the new username while scoreboards don't. **You can fix this by typing `/reload` while the player is online (recommended) or setting their mana to some value like so:**

  `/scoreboard players set @s idea_anvil_mana 0`

![Cookie icon with heading for Credits & License](https://cdn.modrinth.com/data/cached_images/a6fd2f4b99d0eb58ac094d54eccdbcaf2e4687a2_0.webp)

Thanks to the awesome people who made the Minecraft Wiki. My mod has ~9k lines of wiki copy-pasted from there for the AI to use, around 18x more than the 500 lines of Java in the mod.

Thanks to tr7zw for the heading icons and the github and modrinth icons at the top, which are copied from his mod [Entity Culling](https://modrinth.com/mod/entityculling)

This mod is licensed under the MIT license, except the system prompt. See the LICENSE file in the GitHub repo.\
The Minecraft Wiki articles in the system prompt are licensed under the CC BY-NC-SA 3.0 license,\
so the entire system prompt is also licensed under the CC BY-NC-SA 3.0 license.



