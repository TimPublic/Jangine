# ENTITY COMPONENT SYSTEM

The entity component system consists of three layers:
<ul>
<li> System </li>
<li> Processor </li>
<li> Component </li>
</ul>

## System

The system is home to a scene and contains the processors and entities.
It manages the processors, checks their requirements and pushes notifications.
<p></p>
It also manages the entity addition and removal.

## Processor

The processor is managing a set of components,
which is specified by the creator of the processor
in an abstract class.
<p></p>
It holds key-value pairs of components, mapped to entities.
The processor works with a two-step component validation system: <br>
The first step, is to check the active state of every component,
if a component is inactive, it is automatically excluded from the abstract
update method. <br>
The second step is to check every component against an abstract
validation method created by the creator of the processor. <br>
The final validated components are then pushed to the internal abstract
update method.

## Component

The component is just a container of data which must contain an activation
state. Components are managed by processors.