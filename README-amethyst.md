# Budding Polar Amethyst System

Simplified 1.7.10 backport-style implementation of the Amethyst budding mechanic (inspired by Et Futurum Requiem / modern MC) using a single metadata-driven block for all four growth stages.

## Blocks & Items
* `amethyst_block` – decorative storage.
* `budding_amethyst` – emits growth ticks that create/upgrade buds on its six faces.
* `amethyst_cluster` – one block containing 4 visual stages (small / medium / large / full cluster).
* `amethyst_shard` – drop from the fully grown cluster (stage 3).

## Metadata Encoding
Orientation = 0..5 (EnumFacing ordinal). Stage = 0..3.

Formula: `meta = orientation + stage * 6` → valid range 0..23.

Stage meanings:
0: Small Amethyst Bud
1: Medium Amethyst Bud
2: Large Amethyst Bud
3: Amethyst Cluster (final, shard drops)

## Growth Logic
On a random tick (20% chance):
1. Pick a random side S.
2. If a bud/cluster already exists on S:
	* If its stage < 3 and orientation == S → increment to next stage (meta += 6).
3. Else if the space is air → place a stage 0 bud with orientation S (meta = S).

All orientation and stage transitions stay within the single block.

## Drops
Only stage 3 (full cluster) drops shards.
Base: 4 shards. Fortune: small bonus (chance to add +2 per fortune level when lucky).
Earlier stages drop nothing (encourage Silk Touch / patience decisions).

## Creative Inventory
All four stage variants are exposed via the cluster's `ItemBlock` (metas 0,6,12,18).

## File Overview
* `BlockBuddingAmethyst` – tick + growth logic
* `BlockAmethystCluster` – bounding boxes, icons, drop logic
* `ItemBlockAmethystCluster` – subtypes and naming

## Future Ideas
* Configurable growth chance & shard yields
* Bud growth allowed underwater
* Geode worldgen wrapper (not implemented here)


