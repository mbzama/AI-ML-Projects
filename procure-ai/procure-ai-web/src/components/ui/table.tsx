import { Table as MantineTable } from '@mantine/core';
import type { TableProps } from '@mantine/core';
import { forwardRef } from 'react';

interface CustomTableProps extends TableProps {
  children?: React.ReactNode;
}

const Table = forwardRef<HTMLTableElement, CustomTableProps>(
  ({ children, ...props }, ref) => {
    return (
      <MantineTable
        ref={ref}
        striped
        highlightOnHover
        withTableBorder
        withColumnBorders
        {...props}
      >
        {children}
      </MantineTable>
    );
  }
);

Table.displayName = 'Table';

const TableHeader = forwardRef<HTMLTableSectionElement, React.HTMLAttributes<HTMLTableSectionElement>>(
  ({ children, ...props }, ref) => (
    <MantineTable.Thead ref={ref} {...props}>
      {children}
    </MantineTable.Thead>
  )
);

TableHeader.displayName = 'TableHeader';

const TableBody = forwardRef<HTMLTableSectionElement, React.HTMLAttributes<HTMLTableSectionElement>>(
  ({ children, ...props }, ref) => (
    <MantineTable.Tbody ref={ref} {...props}>
      {children}
    </MantineTable.Tbody>
  )
);

TableBody.displayName = 'TableBody';

const TableFooter = forwardRef<HTMLTableSectionElement, React.HTMLAttributes<HTMLTableSectionElement>>(
  ({ children, ...props }, ref) => (
    <MantineTable.Tfoot ref={ref} {...props}>
      {children}
    </MantineTable.Tfoot>
  )
);

TableFooter.displayName = 'TableFooter';

const TableRow = forwardRef<HTMLTableRowElement, React.HTMLAttributes<HTMLTableRowElement>>(
  ({ children, ...props }, ref) => (
    <MantineTable.Tr ref={ref} {...props}>
      {children}
    </MantineTable.Tr>
  )
);

TableRow.displayName = 'TableRow';

const TableHead = forwardRef<HTMLTableCellElement, React.ThHTMLAttributes<HTMLTableCellElement>>(
  ({ children, ...props }, ref) => (
    <MantineTable.Th ref={ref} {...props}>
      {children}
    </MantineTable.Th>
  )
);

TableHead.displayName = 'TableHead';

const TableCell = forwardRef<HTMLTableCellElement, React.TdHTMLAttributes<HTMLTableCellElement>>(
  ({ children, ...props }, ref) => (
    <MantineTable.Td ref={ref} {...props}>
      {children}
    </MantineTable.Td>
  )
);

TableCell.displayName = 'TableCell';

const TableCaption = forwardRef<HTMLTableCaptionElement, React.HTMLAttributes<HTMLTableCaptionElement>>(
  ({ children, ...props }, ref) => (
    <MantineTable.Caption ref={ref} {...props}>
      {children}
    </MantineTable.Caption>
  )
);

TableCaption.displayName = 'TableCaption';

export {
  Table,
  TableHeader,
  TableBody,
  TableFooter,
  TableHead,
  TableRow,
  TableCell,
  TableCaption,
};
