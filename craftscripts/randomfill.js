/*
 * SpartaMC Builds - Random Fill
 * Uso: /cs randomfill <bloco1> <peso1> <bloco2> <peso2> [...]
 * Preenche a selecao com blocos aleatorios usando pesos configurados.
 * Exemplo: /cs randomfill 1 60 4 25 98 15
 *   (60% pedra, 25% cobblestone, 15% stone brick)
 */

importPackage(Packages.com.sk89q.worldedit);
importPackage(Packages.com.sk89q.worldedit.blocks);

var usage = "<bloco1> <peso1> <bloco2> <peso2> ... - Preenchimento aleatorio com pesos";

if (argv.length < 3 || (argv.length - 1) % 2 !== 0) {
    player.printError("Uso: /cs randomfill " + usage);
    player.printError("Exemplo: /cs randomfill 1 60 4 25 98 15");
    throw "Argumentos invalidos";
}

function parseBlock(str) {
    var parts = str.split(":");
    var id = parseInt(parts[0]);
    var data = parts.length > 1 ? parseInt(parts[1]) : 0;
    return new BaseBlock(id, data);
}

var entries = [];
var totalWeight = 0;

for (var i = 1; i < argv.length; i += 2) {
    var b = parseBlock(argv[i]);
    var w = parseInt(argv[i + 1]);
    totalWeight += w;
    entries.push({ block: b, weight: w });
}

function pickBlock() {
    var r = Math.random() * totalWeight;
    var sum = 0;
    for (var i = 0; i < entries.length; i++) {
        sum += entries[i].weight;
        if (r < sum) return entries[i].block;
    }
    return entries[entries.length - 1].block;
}

var sess = context.remember();
var region = context.getSession().getSelection(player.getWorld());
var min = region.getMinimumPoint();
var max = region.getMaximumPoint();

var affected = 0;

for (var x = min.getBlockX(); x <= max.getBlockX(); x++) {
    for (var z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
        for (var y = min.getBlockY(); y <= max.getBlockY(); y++) {
            sess.setBlock(new Vector(x, y, z), pickBlock());
            affected++;
        }
    }
}

var desc = [];
for (var i = 0; i < entries.length; i++) {
    var pct = Math.round(entries[i].weight / totalWeight * 100);
    desc.push(entries[i].block.getId() + ":" + entries[i].block.getData() + " (" + pct + "%)");
}

player.print("\u00A7aRandom fill aplicado! " + affected + " blocos.");
player.print("\u00A77Mix: " + desc.join(", "));
