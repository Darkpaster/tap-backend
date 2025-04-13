package com.human.tapMMO.runtime.game.world;

import lombok.Getter;

@Getter
class CollisionGrid {
    private boolean[][] walkable;
    private int width;
    private int height;

    public CollisionGrid(boolean[][] walkable) {
        this.walkable = walkable;
        this.width = walkable.length;
        this.height = walkable[0].length;
    }

    /**
     * Проверяет, проходима ли позиция
     * @param x координата x
     * @param y координата y
     * @return true если позиция проходима
     */
    public boolean isWalkable(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }
        return walkable[x][y];
    }

    /**
     * Находит путь между двумя точками используя алгоритм A*
     * @param startX начальная координата x
     * @param startY начальная координата y
     * @param endX конечная координата x
     * @param endY конечная координата y
     * @return список позиций, составляющих путь
     */
    public List<Position> findPath(int startX, int startY, int endX, int endY) {
        // Реализация алгоритма A*
        // В этом примере реализация упрощена для ясности

        // Проверка граничных условий
        if (!isWalkable(startX, startY) || !isWalkable(endX, endY)) {
            return new ArrayList<>();
        }

        // Направления движения (8 направлений)
        int[][] dirs = {
                {0, 1}, {1, 0}, {0, -1}, {-1, 0},  // Основные направления
                {1, 1}, {1, -1}, {-1, -1}, {-1, 1} // Диагональные направления
        };

        // Множества для A*
        boolean[][] closed = new boolean[width][height];
        Node[][] nodes = new Node[width][height];

        // Инициализация узлов
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                nodes[x][y] = new Node(x, y);
            }
        }

        // Очередь с приоритетом для открытого множества
        List<Node> openList = new ArrayList<>();

        // Начальный узел
        Node startNode = nodes[startX][startY];
        Node endNode = nodes[endX][endY];

        startNode.g = 0;
        startNode.h = heuristic(startX, startY, endX, endY);
        startNode.f = startNode.h;

        openList.add(startNode);

        while (!openList.isEmpty()) {
            // Сортировка по f-значению
            openList.sort((a, b) -> Float.compare(a.f, b.f));

            // Извлечение узла с наименьшим f-значением
            Node current = openList.remove(0);

            // Если достигли конечного узла, восстанавливаем путь
            if (current.x == endX && current.y == endY) {
                return reconstructPath(nodes, startX, startY, endX, endY);
            }

            // Добавление в закрытое множество
            closed[current.x][current.y] = true;

            // Проверка соседей
            for (int[] dir : dirs) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];

                // Проверка границ и проходимости
                if (!isWalkable(nx, ny) || closed[nx][ny]) {
                    continue;
                }

                // Стоимость движения (для диагоналей - 1.4, для прямых - 1.0)
                float moveCost = (dir[0] != 0 && dir[1] != 0) ? 1.4f : 1.0f;
                float newG = current.g + moveCost;

                Node neighbor = nodes[nx][ny];

                // Если узел уже в открытом множестве, но новый путь хуже, пропускаем
                if (openList.contains(neighbor) && newG >= neighbor.g) {
                    continue;
                }

                // Обновляем узел
                neighbor.parent = current;
                neighbor.g = newG;

                if (!openList.contains(neighbor)) {
                    neighbor.h = heuristic(nx, ny, endX, endY);
                    neighbor.f = neighbor.g + neighbor.h;
                    openList.add(neighbor);
                }
            }
        }

        // Путь не найден
        return new ArrayList<>();
    }

    /**
     * Эвристическая функция для A* (расстояние по Манхэттену)
     */
    private float heuristic(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * Восстанавливает путь от конечной до начальной точки
     */
    private List<Position> reconstructPath(Node[][] nodes, int startX, int startY, int endX, int endY) {
        List<Position> path = new ArrayList<>();
        Node current = nodes[endX][endY];

        while (current != null && !(current.x == startX && current.y == startY)) {
            path.add(0, new Position(current.x, current.y, 0));
            current = current.parent;
        }

        return path;
    }

    /**
     * Вспомогательный класс для A*
     */
    private static class Node {
        int x, y;
        float g; // Стоимость от начала до текущего узла
        float h; // Эвристическая стоимость до конечного узла
        float f; // g + h
        Node parent;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
            this.g = Float.MAX_VALUE;
            this.h = 0;
            this.f = Float.MAX_VALUE;
            this.parent = null;
        }
    }
}