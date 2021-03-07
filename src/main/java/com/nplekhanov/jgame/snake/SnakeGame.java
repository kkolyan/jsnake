package com.nplekhanov.jgame.snake;

import com.nplekhanov.jgame.RVector2Int;
import com.nplekhanov.jgame.Vector2Int;
import com.nplekhanov.swing.Application;
import com.nplekhanov.swing.ApplicationEnv;
import com.nplekhanov.swing.Key;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

public class SnakeGame implements Application {
    private static final int START_LENGTH = 3;
    private static final double ACCELERATION = 0.95;
    public Node[] nodes;
    public Node head;
    public Node tail;
    public Vector2Int food;


    private final RVector2Int fieldSize;

    private long lastFrameNanos;
    private long nonSpentNanos;
    private long nanosForStep;

    private final Random random = new Random();

    private final Vector2Int direction = new Vector2Int();
    private final Vector2Int desiredDirection = new Vector2Int();

    private final Vector2Int nextHeadPos = new Vector2Int();
    private final Vector2Int nextFoodPos = new Vector2Int();

    public SnakeGame(final RVector2Int fieldSize) {
        this.fieldSize = fieldSize;
        newGame();
    }

    private void newGame() {
        nodes = new Node[fieldSize.getX() * fieldSize.getY()];
        head = new Node();
        tail = head;
        for (int i = 0; i < START_LENGTH; i++) {
            addTail();
        }
        nanosForStep = 160_000_000L;
        lastFrameNanos = 0;
        desiredDirection.set(1, 0);
        direction.set(desiredDirection);
        food = null;
        spawnFood();
    }

    private int getIndex(final RVector2Int pos) {
        checkBounds(pos);
        return pos.getX() + pos.getY() * fieldSize.getX();
    }

    private void checkBounds(final RVector2Int pos) {
        if (pos.getX() < 0 || pos.getX() >= fieldSize.getX() || pos.getY() < 0 || pos.getY() >= fieldSize.getY()) {
            throw new IllegalArgumentException(pos.toString());
        }
    }

    private void spawnFood() {
        while (true) {
            nextFoodPos.set(
                random.nextInt(fieldSize.getX()),
                random.nextInt(fieldSize.getY())
            );
            int index = getIndex(nextFoodPos);
            Node node = nodes[index];
            if (node == null) {
                food = new Vector2Int(nextFoodPos.getX(), nextFoodPos.getY());
                break;
            }
        }
    }

    @Override
    public String getTitle() {
        return "JGame";
    }

    @Override
    public void update(final Graphics2D canvas, final ApplicationEnv env) {
        long nanosNow = System.nanoTime();
        if (lastFrameNanos == 0) {
            lastFrameNanos = nanosNow;
        }
        long deltaNanos = nanosNow - lastFrameNanos;
        lastFrameNanos = nanosNow;

        updateLogic(env, deltaNanos);
        render(env, canvas);
    }

    private void updateLogic(final ApplicationEnv env, final long deltaNanos) {
        if (env.keyboard.isPressed(Key.W)) desiredDirection.set(+0, -1);
        if (env.keyboard.isPressed(Key.S)) desiredDirection.set(+0, +1);
        if (env.keyboard.isPressed(Key.A)) desiredDirection.set(-1, +0);
        if (env.keyboard.isPressed(Key.D)) desiredDirection.set(+1, +0);

        nonSpentNanos += deltaNanos;
        while (nonSpentNanos > nanosForStep) {
            nonSpentNanos -= nanosForStep;
            doStep(env);
        }
    }

    private void doStep(final ApplicationEnv env) {

        if (Math.abs(desiredDirection.x - direction.x) < 2 && Math.abs(desiredDirection.y - direction.y) < 2) {
            direction.set(desiredDirection);
        }

        nextHeadPos.set(head.position);
        nextHeadPos.add(direction);
        warp(nextHeadPos);

        Node obstacle = nodes[getIndex(nextHeadPos)];
        if (obstacle == null) {
            boolean foodReached = nextHeadPos.equals(food);
            if (foodReached) {
                food = null;

                addTail();

                nanosForStep = (long) (nanosForStep * ACCELERATION);

                spawnFood();
            }

            for (Node node = tail; node.head != null; node = node.head) {
                moveNode(node, node.head.position);
            }
            moveNode(head, nextHeadPos);
        } else {
            newGame();
        }
    }

    private void addTail() {
        Node newTail = new Node();
        newTail.head = tail;
        moveNode(newTail, newTail.position);
        tail = newTail;

    }

    private void warp(final Vector2Int pos) {
        while (pos.x < 0) pos.x += fieldSize.getX();
        while (pos.y < 0) pos.y += fieldSize.getY();
        while (pos.x >= fieldSize.getX()) pos.x -= fieldSize.getX();
        while (pos.y >= fieldSize.getY()) pos.y -= fieldSize.getY();
        checkBounds(pos);
    }

    private void moveNode(final Node node, final Vector2Int target) {
        if (nodes[getIndex(node.position)] == node) {
            nodes[getIndex(node.position)] = null;
        }
        node.position.set(target);
        nodes[getIndex(node.position)] = node;
    }

    private void render(final ApplicationEnv env, final Graphics2D canvas) {
        float cellW = 1f * env.width / fieldSize.getX();
        float cellH = 1f * env.height / fieldSize.getY();

        canvas.setBackground(Color.black);
        canvas.clearRect(0, 0, env.width, env.height);

        canvas.setColor(Color.white);
        for (Node node = tail; node != null; node = node.head) {
            canvas.fillRect(
                (int) (node.position.x * cellW),
                (int) (node.position.y * cellH),
                (int) cellW,
                (int) cellH
            );
        }
        if (food != null) {
            canvas.setColor(Color.green);
            canvas.fillRect(
                (int) (food.x * cellW),
                (int) (food.y * cellH),
                (int) cellW,
                (int) cellH
            );
        }
    }

    @Override
    public void destroy() {
    }
}
