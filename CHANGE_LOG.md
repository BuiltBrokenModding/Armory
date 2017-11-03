# INFO
This log contains changes made to the project. Each entry contains changed made after the last version but before the number was changed. Any changes made after a number change are considered part of the next release. This is regardless if versions are still being released with that version number attached. 

If this is a problem, use exact build numbers to track changes. As each build logs the git-hash it was created from to better understand changes made.

# Versions
# 0.7.4
### Runtime Changes
Added: Impact damage type (used for bullets)
Fixed: Owner check on sentries
Fixed: Ghost entities hanging around
Fixed: Reload time being in seconds instead of ticks
Fixed: Ammo check searching all of player inventory
Fixed: Crash issue while searching for ammo (odd mod compat issue)
Fixed: Ammo clipping being inserted in head slot (odd mod compat issue)
Fixed: Sentry droping with stack size greater than zero
Fixed: All damage types bypassing armor
Fixed: bullets sending target to the moon (aka removed knockback)
Fixed: NPE crash when opening GUI on a sentry without data
Fixed: Sentry not dropping with data (actual fix in Voltz Engine)


### Develoment Changes
Changed: Ammo search to only do main inventory
        
## 0.7.3
### Runtime Changes
Added: test tools & weapons for new melee system

### Develoment Changes
Added: Melee tools
        can configure blocks & materials to break
        can configure speed to break blocks
        can configure enchant power
        can configure durability
        can configure durability loss on hitting entities
Added: Melee weapons (extends tools)
        can configure damage when attacking entities
        
# Versions
## 0.7.2
### Runtime Changes
Added: "Not currently Implemented" warnings to unfinished GUI sections
Fixed: tooltips on sentry's target settings GUI


### Develoment Changes


## 0.7.1
### Runtime Changes
Updated to lasted VoltzEngine version

### Develoment Changes
Added: Throw power to throwable weapons
Added: Creative tab selection to data entries (guns, ammo, etc)

### Before last
Change log is a work in progress, not everything will be logged due to time.