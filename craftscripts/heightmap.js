/*
 * (c) 2026 Sparta Network. - zrfadev
 * SpartaMC Builds - Heightmap Raise/Lower
 * Uso: /bs heightmap <raise|lower|smooth> [intensidade]
 *   raise = eleva o terreno no centro da selecao (formato de colina)
 *   lower = rebaixa o terreno no centro (formato de cratera)
 *   smooth = suaviza irregularidades do terreno
 *   intensidade = altura maxima do efeito (padrao: 5)
 * Exemplo: /bs heightmap raise 8
 */

importPackage(Packages.com.sk89q.worldedit);
importPackage(Packages.com.sk89q.worldedit.blocks);

var usage = "<raise|lower|smooth> [intensidade] - Modifica altura do terreno";

context.checkArgs(1, 2, usage);

var mode = argv[1].toLowerCase();
var intensity = argv.length > 2 ? parseInt(argv[2]) : 5;

if (mode !== "raise" && mode !== "lower" && mode !== "smooth") {
    player.printError("Modo invalido. Use: raise, lower ou smooth");
    throw "Modo invalido";
}

var STONE = new BaseBlock(1);
var AIR = new BaseBlock(0);

var sess = context.remember();
var region = context.getSession().getSelection(player.getWorld());
var min = region.getMinimumPoint();
var max = region.getMaximumPoint();

var centerX = (min.getBlockX() + max.getBlockX()) / 2.0;
var centerZ = (min.getBlockZ() + max.getBlockZ()) / 2.0;
var radiusX = (max.getBlockX() - min.getBlockX()) / 2.0;
var radiusZ = (max.getBlockZ() - min.getBlockZ()) / 2.0;
var maxRadius = Math.max(radiusX, radiusZ);

var affected = 0;

if (mode === "smooth") {
    for (var x = min.getBlockX(); x <= max.getBlockX(); x++) {
        for (var z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
            var heights = [];
            for (var dx = -1; dx <= 1; dx++) {
                for (var dz = -1; dz <= 1; dz++) {
                    var sx = x + dx;
                    var sz = z + dz;
                    for (var y = max.getBlockY(); y >= min.getBlockY(); y--) {
                        if (sess.getBlock(new Vector(sx, y, sz)).getId() !== 0) {
                            heights.push(y);
                            break;
                        }
                    }
                }
            }

            if (heights.length === 0) continue;

            var avg = 0;
            for (var i = 0; i < heights.length; i++) avg += heights[i];
            avg = Math.round(avg / heights.length);

            var currentTop = -1;
            for (var y = max.getBlockY(); y >= min.getBlockY(); y--) {
                if (sess.getBlock(new Vector(x, y, z)).getId() !== 0) {
                    currentTop = y;
                    break;
                }
            }

            if (currentTop < min.getBlockY()) continue;

            if (avg > currentTop) {
                for (var y = currentTop + 1; y <= avg; y++) {
                    sess.setBlock(new Vector(x, y, z), STONE);
                    affected++;
                }
            } else if (avg < currentTop) {
                for (var y = currentTop; y > avg; y--) {
                    sess.setBlock(new Vector(x, y, z), AIR);
                    affected++;
                }
            }
        }
    }
} else {
    for (var x = min.getBlockX(); x <= max.getBlockX(); x++) {
        for (var z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
            var dx = (x - centerX) / (radiusX > 0 ? radiusX : 1);
            var dz = (z - centerZ) / (radiusZ > 0 ? radiusZ : 1);
            var dist = Math.sqrt(dx * dx + dz * dz);

            if (dist > 1.0) continue;

            var factor = (1.0 - dist * dist);
            var delta = Math.round(intensity * factor);
            if (delta < 1) continue;

            var topY = -1;
            for (var y = max.getBlockY(); y >= min.getBlockY(); y--) {
                if (sess.getBlock(new Vector(x, y, z)).getId() !== 0) {
                    topY = y;
                    break;
                }
            }

            if (topY < min.getBlockY()) continue;

            if (mode === "raise") {
                for (var d = 1; d <= delta; d++) {
                    var ny = topY + d;
                    if (ny <= max.getBlockY()) {
                        sess.setBlock(new Vector(x, ny, z), STONE);
                        affected++;
                    }
                }
            } else {
                for (var d = 0; d < delta; d++) {
                    var ny = topY - d;
                    if (ny >= min.getBlockY()) {
                        sess.setBlock(new Vector(x, ny, z), AIR);
                        affected++;
                    }
                }
            }
        }
    }
}

player.print("\u00A7aHeightmap [" + mode + "] aplicado! " + affected + " blocos alterados.");
