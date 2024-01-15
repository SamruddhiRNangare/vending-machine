import { MachineContext } from './machine-context';
import { Item } from './item';

export class State {
    type: string;
    status: string;
    item: Item;
    value: number;
    machineContext: MachineContext;
}
